package com.trendmicro.browser.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.trendmicro.browser.R;
import com.trendmicro.browser.browser.AdBlock;
import com.trendmicro.browser.database.RecordAction;
import com.trendmicro.browser.unit.BrowserUnit;
import com.trendmicro.browser.view.NinjaToast;
import com.trendmicro.browser.view.WhitelistAdapter;

import java.util.List;

public class WhitelistActivity extends Activity {
    private WhitelistAdapter adapter;
    private List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whitelist);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        RecordAction action = new RecordAction(this);
        action.open(false);
        list = action.listDomains();
        action.close();

        ListView listView = (ListView) findViewById(R.id.whitelist);
        listView.setEmptyView(findViewById(R.id.whitelist_empty));

        adapter = new WhitelistAdapter(this, R.layout.whitelist_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final EditText editText = (EditText) findViewById(R.id.whilelist_edit);
        showSoftInput(editText);

        Button button = (Button) findViewById(R.id.whilelist_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = editText.getText().toString().trim();
                if (domain.isEmpty()) {
                    NinjaToast.show(WhitelistActivity.this, R.string.toast_input_empty);
                } else if (!BrowserUnit.isURL(domain)) {
                    NinjaToast.show(WhitelistActivity.this, R.string.toast_invalid_domain);
                } else {
                    RecordAction action = new RecordAction(WhitelistActivity.this);
                    action.open(true);
                    if (action.checkDomain(domain)) {
                        NinjaToast.show(WhitelistActivity.this, R.string.toast_domain_already_exists);
                    } else {
                        AdBlock adBlock = new AdBlock(WhitelistActivity.this);
                        adBlock.addDomain(domain.trim());
                        list.add(0, domain.trim());
                        adapter.notifyDataSetChanged();
                        NinjaToast.show(WhitelistActivity.this, R.string.toast_add_whitelist_successful);
                    }
                    action.close();
                }
            }
        });
    }

    @Override
    public void onPause() {
        hideSoftInput(findViewById(R.id.whilelist_edit));
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.whilelist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int i = menuItem.getItemId();
        if (i == android.R.id.home) {
            finish();

        } else if (i == R.id.whitelist_menu_clear) {
            AdBlock adBlock = new AdBlock(this);
            adBlock.clearDomains();
            list.clear();
            adapter.notifyDataSetChanged();

        } else {
        }
        return true;
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
