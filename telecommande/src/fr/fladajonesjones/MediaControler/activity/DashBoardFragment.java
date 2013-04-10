package fr.fladajonesjones.MediaControler.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.fladajonesjones.MediaControler.R;
import fr.fladajonesjones.MediaControler.adapter.MyPagerAdapter;
import fr.fladajonesjones.MediaControler.menu.MenuDrawerUtil;

public class DashBoardFragment extends Fragment {

	private PagerAdapter mPagerAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View layout = inflater.inflate(R.layout.fragment_dashboard, null);

		// Création de la liste de Fragments que fera défiler le PagerAdapter
		List<Fragment> fragments = new ArrayList<Fragment>();

		// Ajout des Fragments dans la liste
		fragments.add(Fragment.instantiate(getActivity(),RendererGridFragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),DeviceFragment.class.getName()));

		// Création de l'adapter qui s'occupera de l'affichage de la liste de
		// Fragments
		this.mPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(), fragments);

		ViewPager pager = (ViewPager) layout.findViewById(R.id.viewpager);
		// Affectation de l'adapter au ViewPager
		pager.setAdapter(this.mPagerAdapter);
		return layout;
	}
	
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuDrawerUtil.toggleMenu();
    }
}
