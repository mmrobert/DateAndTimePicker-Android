package com.homearound.www.homearound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class RoleChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_choose);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_role_choose);
        setSupportActionBar(toolbar);

        LinearLayout customerL = (LinearLayout)findViewById(R.id.lout_role_choose_customer);
        LinearLayout professionalL = (LinearLayout)findViewById(R.id.lout_role_choose_professional);

        customerL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleChooseActivity.this, RegisterActivity.class);
                //  Intent intent = new Intent(this, SignupActivity.class);
                intent.putExtra("role", "customer");
                Context context = RoleChooseActivity.this;
                context.startActivity(intent);
                finish();
            }
        });
        professionalL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoleChooseActivity.this, RegisterActivity.class);
                //  Intent intent = new Intent(this, SignupActivity.class);
                intent.putExtra("role", "merchant");
                Context context = RoleChooseActivity.this;
                context.startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_role_choose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel_role_choose) {
            Intent intent = new Intent(this, LoginActivity.class);
            //  Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
