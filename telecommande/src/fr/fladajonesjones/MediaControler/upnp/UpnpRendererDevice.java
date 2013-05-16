package fr.fladajonesjones.MediaControler.upnp;

import java.util.Timer;
import java.util.TimerTask;

import fr.fladajonesjones.media.model.Piste;
import fr.fladajonesjones.media.model.Radio;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.*;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.TransportState;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.TransportInfo;

import fr.fladajonesjones.MediaControler.Application;
import fr.fladajonesjones.MediaControler.database.PisteDAO;
import fr.fladajonesjones.MediaControler.events.UpnpRendererMetaChangeEvent;
import fr.fladajonesjones.MediaControler.events.UpnpRendererStatutChangeEvent;
import fr.fladajonesjones.MediaControler.manager.UpnpDeviceManager;
import fr.fladajonesjones.media.model.Album;
import fr.fladajonesjones.media.model.Musique;
import fr.flagadajones.media.util.BusManager;
import fr.flagadajones.media.util.UpnpTransformer;

public class UpnpRendererDevice extends UpnpDevice {
	Service<Device, Service> transPortService = null;
	Musique musique;
	public PositionInfo positionInfo;
	MediaInfo mediaInfo;
	TransportInfo transportInfo;
	public String statut;
	public boolean playing = false;
	public boolean connected = false;
	SubscriptionCallback transPortServicecallback = null;

	public UpnpRendererDevice() {

	}

	public Musique getMusique() {
		return musique;
	}

	public long getPosition() {
		return 0;
	}

	public UpnpRendererDevice(Device device) {
		super(device);
		if (device.isFullyHydrated()) {
		//	if (transPortService == null)
				transPortService = device.findService(new UDAServiceType(
						"AVTransport"));
			initSubscription();
			setSubscriptionCallback();

		}
	}

	public void setDevice(Device device) {
		super.setDevice(device);
		if (device.isFullyHydrated()) {
		//	if (transPortService == null)
				transPortService = device.findService(new UDAServiceType(
						"AVTransport"));
			initSubscription();
			setSubscriptionCallback();
		}
	}

	public boolean isPlaying() {
		return playing;
	}

	public boolean isConnected() {
		return connected;
	}


	public void playMusique(Musique musique) {
		setTransportURI(musique);
		this.musique = musique;
	}

	public void setTransportURI(Musique musique) {
		//if (transPortService == null)
			transPortService = device.findService(new UDAServiceType(
					"AVTransport"));
		// this.musique = musique;
		// TODO: Si liste piste vide get liste piste

		if (musique instanceof Album) {
			Album album = (Album) musique;

			if (!album.isPisteLoaded()) {
				PisteDAO pisteDAO = new PisteDAO();
				album.setPistes(pisteDAO.getAllPistes(album.upnpId));
			}
		}
        String metaData="NO_METADATA";
        if(musique instanceof Album){
            metaData    =UpnpTransformer.albumToMetaData((Album) musique);
        }
        else if (musique instanceof Piste){
           metaData=UpnpTransformer.pisteToMetaData((Piste) musique);
        }
        else { //radio
            metaData=UpnpTransformer.radioToMetaData((Radio) musique);
        }
		ActionCallback setAVTransportURIAction = new SetAVTransportURI(
				transPortService, musique.getUrl(),
				metaData) {
			@Override
			public void success(ActionInvocation invocation) {

				super.success(invocation);
				play();
				BusManager.getInstance().post(
						new UpnpRendererStatutChangeEvent());

			}

			@Override
			public void failure(ActionInvocation invocation,
					UpnpResponse operation, String defaultMsg) {
				Application.activity.showToast(defaultMsg, true);
				playing = false;
			}

		};
		UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(
				setAVTransportURIAction);

	}

	public void getTransportInfo() {
		//if (transPortService == null)
			transPortService = device.findService(new UDAServiceType(
					"AVTransport"));
		final UpnpRendererDevice device = this;
		ActionCallback setAVTransportURIAction = new GetTransportInfo(
				transPortService) {

			@Override
			public void failure(ActionInvocation invocation,
					UpnpResponse operation, String defaultMsg) {
                stopRepeatingTask();
                Application.activity.showToast(defaultMsg, true);

				connected=false;

			}

			@Override
			public void received(ActionInvocation invocation,
					TransportInfo transportInfo) {
				// TODO Auto-generated method stub
				device.transportInfo = transportInfo;
				if (transportInfo.getCurrentTransportState() == org.fourthline.cling.support.model.TransportState.PLAYING)
					device.playing = true;
				else
					device.playing = false;
				BusManager.getInstance()
						.post(new UpnpRendererMetaChangeEvent(UpnpRendererDevice.this));
			}
		};
		UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(
				setAVTransportURIAction);

	}

	public void getPositionInfo() {
		//if (transPortService == null)
			transPortService = device.findService(new UDAServiceType(
					"AVTransport"));
		final UpnpRendererDevice device = this;
		ActionCallback setAVTransportURIAction = new GetPositionInfo(
				transPortService) {

			@Override
			public void failure(ActionInvocation invocation,
					UpnpResponse operation, String defaultMsg) {
				Application.activity.showToast(defaultMsg, true);
				stopRepeatingTask();
				// connected=false;

			}

			@Override
			public void received(ActionInvocation invocation,
					PositionInfo positionInfo) {
				// TODO Auto-generated method stub
				device.positionInfo = positionInfo;
				BusManager.getInstance()
						.post(new UpnpRendererMetaChangeEvent(UpnpRendererDevice.this));
			}
		};
		UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(
				setAVTransportURIAction);

	}

	public void getMediaInfo() {
		//if (transPortService == null)
			transPortService = device.findService(new UDAServiceType(
					"AVTransport"));
		final UpnpRendererDevice device = this;
		ActionCallback setAVTransportURIAction = new GetMediaInfo(
				transPortService) {

			@Override
			public void failure(ActionInvocation invocation,
					UpnpResponse operation, String defaultMsg) {
				Application.activity.showToast(defaultMsg, true);
				stopRepeatingTask();
				// connected=false;

			}

			@Override
			public void received(ActionInvocation invocation,
					MediaInfo mediaInfo) {
				device.mediaInfo = mediaInfo;
				device.musique = UpnpTransformer.metaDataToMusique(mediaInfo
						.getCurrentURIMetaData());

				BusManager.getInstance()
						.post(new UpnpRendererMetaChangeEvent(UpnpRendererDevice.this));
			}
		};
		UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(
				setAVTransportURIAction);

	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		super.setSelected(selected);
		initSubscription();
		setSubscriptionCallback();
	}

	private void setSubscriptionCallback() {
		if (device != null)
			if (selected && device.isFullyHydrated())
				UpnpDeviceManager.getInstance().upnpService.getControlPoint()
						.execute(transPortServicecallback);
			else if (!selected && transPortServicecallback != null
					&& transPortServicecallback.getSubscription() != null) {
				transPortServicecallback.end();
				// UpnpDeviceManager.getInstance().upnpService
				// .getRegistry()
				// .removeRemoteSubscription(
				// (RemoteGENASubscription) transPortServicecallback
				// .getSubscription());
				connected = false;

			}
	}

	// Each control point can poll for these values at a rate appropriate for
	// their application, whenever they need
	// to. For example, a control point can invoke GetPositionInfo every second
	// when the TransportState is
	// PLAYING, RECORDING or TRANSITIONING. This is more efficient and flexible
	// than requiring event
	// notifications to be sent to all subscribing control points, in all cases.

	public void play() {
		//if (transPortService == null)
			transPortService = device.findService(new UDAServiceType(
					"AVTransport"));

		ActionCallback playAction = new Play(transPortService) {
			@Override
			public void success(ActionInvocation invocation) {
				// TODO Auto-generated method stub
				super.success(invocation);
				playing = true;
				startRepeatingTask();

			}

			@Override
			public void failure(ActionInvocation invocation,
					UpnpResponse operation, String defaultMsg) {
				Application.activity.showToast(defaultMsg, true);
			}
		};

		UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(
				playAction);
	}


    public void stop(){
     //   if (transPortService == null)
            transPortService = device.findService(new UDAServiceType(
                    "AVTransport"));

        ActionCallback action = new Stop(transPortService) {
            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                playing = true;
                startRepeatingTask();

            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation, String defaultMsg) {
                Application.activity.showToast(defaultMsg, true);
            }
        };

        UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(
                action);

    }

    public void seekPiste(int position){
        seekTo(SeekMode.TRACK_NR,position);
    }
    public void seekPos(int position){
        seekTo(SeekMode.ABS_COUNT,position);
    }
    public void seekTo(SeekMode mode, int position){
      //  if (transPortService == null)
            transPortService = device.findService(new UDAServiceType(
                    "AVTransport"));

        ActionCallback action = new Seek(transPortService, mode,String.valueOf(position)) {
            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                playing = true;
                startRepeatingTask();

            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation, String defaultMsg) {
                Application.activity.showToast(defaultMsg, true);
            }
        };

        UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(
                action);

    }

    public void pause(){
       // if (transPortService == null)
            transPortService = device.findService(new UDAServiceType(
                    "AVTransport"));

        ActionCallback pauseAction = new Pause(transPortService) {
            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                playing = true;
                startRepeatingTask();

            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation, String defaultMsg) {
                Application.activity.showToast(defaultMsg, true);
            }
        };

        UpnpDeviceManager.getInstance().upnpService.getControlPoint().execute(
                pauseAction);

    }

	public void initSubscription() {
		if (transPortServicecallback == null && transPortService != null)
			transPortServicecallback = new SubscriptionCallback(
					transPortService, 600) {

				@Override
				public void established(GENASubscription sub) {
					connected = true;
					BusManager.getInstance().post(
							new UpnpRendererStatutChangeEvent());
					// Application.activity.showToast(
					// "Established: " + sub.getSubscriptionId(), true);
					startRepeatingTask();
				}

				@Override
				protected void failed(GENASubscription subscription,
						UpnpResponse responseStatus, Exception exception,
						String defaultMsg) {
					connected = false;
					BusManager.getInstance().post(
							new UpnpRendererStatutChangeEvent());
					Application.activity.showToast(defaultMsg, true);
				}

				@Override
				public void ended(GENASubscription sub, CancelReason reason,
						UpnpResponse response) {
					// if (reason != null)
					// Application.activity.showToast(reason.toString(), true);
					// else
					// Application.activity.showToast("Ended", true);
					BusManager.getInstance().post(
							new UpnpRendererStatutChangeEvent());
					connected = false;
                    stopRepeatingTask();
				}

				public void eventReceived(GENASubscription sub) {

					LastChange lastChange = null;
					// Application.activity.showToast("Event: "
					// + sub.getCurrentSequence().getValue(), true);
					try {
						lastChange = new LastChange(
								new AVTransportLastChangeParser(), sub
										.getCurrentValues().get("LastChange")
										.toString());
						if (lastChange.getInstanceIDs().length != 0) {
							TransportState transportState = lastChange
									.getEventedValue(
											lastChange.getInstanceIDs()[0],
											AVTransportVariable.TransportState.class);
							if (transportState != null){
                                transportInfo =new TransportInfo(transportState.getValue());
                            }
                            AVTransportVariable.CurrentTrackURI currentTrackURI=
                                    lastChange.getEventedValue(
                                            lastChange.getInstanceIDs()[0], AVTransportVariable.CurrentTrackURI.class);
                            AVTransportVariable.AVTransportURI avTransportURI=
                                    lastChange.getEventedValue(
                                            lastChange.getInstanceIDs()[0], AVTransportVariable.AVTransportURI.class);

                            log.warning(lastChange.toString());

							// TODO : gerer les changements autrement qu'avec un
							// intent
							BusManager.getInstance().post(
									new UpnpRendererStatutChangeEvent());
						}
					} catch (Exception ex) {
						log.warning("Error parsing LastChange event content: "
								+ ex);
						return;
					}

					/*
					 * Map<String, StateVariableValue> values =
					 * sub.getCurrentValues(); StateVariableValue status =
					 * values.get("Status");
					 * assertEquals(status.getDatatype().getClass(),
					 * BooleanDatatype.class);
					 * assertEquals(status.getDatatype().getBuiltin(),
					 * Datatype.Builtin.BOOLEAN);
					 * System.out.println("Status is: " + status.toString());
					 */
				}

				public void eventsMissed(GENASubscription sub,
						int numberOfMissedEvents) {
					Application.activity.showToast("Missed events: "
							+ numberOfMissedEvents, true);
				}

			};
	}

	private final static int INTERVAL = 2000;// * 60 * 2; //2 minutes
	// Handler m_handler = new Handler();

	Runnable m_handlerTask = new Runnable() {
		@Override
		public void run() {
			getMediaInfo();
			getPositionInfo();
			getTransportInfo();
			// m_handler.postDelayed(m_handlerTask, INTERVAL);
		}
	};

	void startRepeatingTask() {
		// Set the schedule function and rate
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// Called each time when 1000 milliseconds (1 second) (the
				// period parameter)
				getMediaInfo();
//				getPositionInfo();
				getTransportInfo();
			}

		},
		// Set how long before to start calling the TimerTask (in milliseconds)
				0,
				// Set the amount of time between each execution (in
				// milliseconds)
				INTERVAL*5);
		t1.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// Called each time when 1000 milliseconds (1 second) (the
				// period parameter)
//				getMediaInfo();
				getPositionInfo();
//				getTransportInfo();
			}

		},
		// Set how long before to start calling the TimerTask (in milliseconds)
				0,
				// Set the amount of time between each execution (in
				// milliseconds)
				INTERVAL);

		
		// m_handlerTask.run();
	}

	void stopRepeatingTask() {
		t.purge();
		t1.purge();
	//	t2.purge();
		// m_handler.removeCallbacks(m_handlerTask);
	}

	Timer t = new Timer();
	Timer t1 = new Timer();
//	Timer t2 = new Timer();
}
