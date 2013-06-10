package fr.flagadajones.widget.testseekbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import com.example.testseekbar.R;

import java.util.ArrayList;

public class MainActivity3 extends Activity {

    //    private CardUI mPlayerCardView;
//    private CardUI mPisteCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_test);

        // init CardView
//        mPlayerCardView = (CardUI) findViewById(R.id.playercardview);
//        mPlayerCardView.setSwipeable(false);
//        mPlayerCardView.addCard(new PlayerCard());


//        mPisteCardView = (CardUI) findViewById(R.id.pistecardsview);
//        mPisteCardView.setSwipeable(false);

        //       stack2.setTitle("Pistes");
//        stack2.setColor("#222222");
//        mPlayerCardView.addStack(stack2);

//        mPisteCardView.addCardToLastStack(new PisteCard("titre1", "04:01"));
//        mPisteCardView.addCardToLastStack(new PisteCard("titre2", "02:01"));
//        mPisteCardView.addCardToLastStack(new PisteCard("titre3", "03:01"));
//        mPisteCardView.addCardToLastStack(new PisteCard("titre4", "06:01"));
//        mPisteCardView.addCardToLastStack(new PisteCard("titre5", "07:01"));


        ArrayList<Piste> piste = new ArrayList<Piste>();

        piste.add(new Piste("titre1", "04:01"));
        piste.add(new Piste("titre2", "02:01"));
        piste.add(new Piste("titre3", "03:01"));
        piste.add(new Piste("titre4", "06:01"));
        piste.add(new Piste("titre5", "07:01"));

        ListView liste = (ListView) findViewById(R.id.pisteListe);
        PisteRawAdapter adapter = new PisteRawAdapter(this);
        adapter.addAll(piste);
        liste.setAdapter(adapter);

        liste.setSelected(true);
        liste.requestFocus();
        liste.setSelection(2);
        liste.setItemChecked(2, true);
        adapter.notifyDataSetChanged();


//        // add AndroidViews Cards
//        
//        // draw cards
        //  mPlayerCardView.refresh();

        //       mPisteCardView.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
