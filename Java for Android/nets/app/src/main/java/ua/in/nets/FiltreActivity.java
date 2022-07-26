package ua.in.nets;

import android.os.Bundle;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class FiltreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtre);

        String[] profession = {"Ремонт бытовой техники, компьютеров и пр.", "Строительство, ремонтные работы",
                "Сфера услуг", "Товары", "Аренда жилья", "Полезные номера", "Полезные номера - мед. учреждения", "Отдых"};
        final RadioButton mProff1;
        final RadioButton mProff2;
        final RadioButton mProff3;
        final RadioButton mProff4;
        final RadioButton mProff5;
        final RadioButton mProff6;
        final RadioButton mProff7;
        final RadioButton mProff8;

        mProff1 = (RadioButton) findViewById(R.id.radioButton1);
        mProff2 = (RadioButton) findViewById(R.id.radioButton2);
        mProff3 = (RadioButton) findViewById(R.id.radioButton3);
        mProff4 = (RadioButton) findViewById(R.id.radioButton4);
        mProff5 = (RadioButton) findViewById(R.id.radioButton5);
        mProff6 = (RadioButton) findViewById(R.id.radioButton6);
        mProff7 = (RadioButton) findViewById(R.id.radioButton7);
        mProff8 = (RadioButton) findViewById(R.id.radioButton8);

        mProff1.setText(profession[0]);
        mProff2.setText(profession[1]);
        mProff3.setText(profession[2]);
        mProff4.setText(profession[3]);
        mProff5.setText(profession[4]);
        mProff6.setText(profession[5]);
        mProff7.setText(profession[6]);
        mProff8.setText(profession[7]);
    }
}
