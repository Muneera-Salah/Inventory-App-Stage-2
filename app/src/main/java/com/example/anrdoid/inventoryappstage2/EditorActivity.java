package com.example.anrdoid.inventoryappstage2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.anrdoid.inventoryappstage2.data.InventoryContract.InventoryEntry;


/**
 * Created by Labtop on 20/02/18.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentProductUri;

    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductQuantityEditText;
    private Spinner mProductSupplieNameSpinner;
    private EditText mProductSupplierPhoneNumberEditText;

    private int mSupplieName = InventoryEntry.SUPPLIER_UNKNOWN;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            Log.d("message", "onTouch");

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Log.d("message", "onCreate");

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        mProductNameEditText = findViewById(R.id.product_name_edit_text);
        mProductPriceEditText = findViewById(R.id.product_price_edit_text);
        mProductQuantityEditText = findViewById(R.id.product_quantity_edit_text);
        mProductSupplieNameSpinner = findViewById(R.id.product_supplier_name_spinner);
        mProductSupplierPhoneNumberEditText = findViewById(R.id.product_supplier_phone_number_edit_text);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mProductSupplieNameSpinner.setOnTouchListener(mTouchListener);
        mProductSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    private void setupSpinner() {
        Log.d("message", "setupSpinner");

        ArrayAdapter productSupplieNameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);

        productSupplieNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mProductSupplieNameSpinner.setAdapter(productSupplieNameSpinnerAdapter);

        mProductSupplieNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_amazon))) {
                        mSupplieName = InventoryEntry.SUPPLIER_AMAZON;
                    } else if (selection.equals(getString(R.string.supplier_jarirr))) {
                        mSupplieName = InventoryEntry.SUPPLIER_JARIRR;
                    } else if (selection.equals(getString(R.string.supplier_obeikan))) {
                        mSupplieName = InventoryEntry.SUPPLIER_OBEIKAN;
                    } else {
                        mSupplieName = InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplieName = InventoryEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    private void insertProduct() {
        Log.d("message", "insertProduct");

        String productNameString = mProductNameEditText.getText().toString().trim();
        String productPriceString = mProductPriceEditText.getText().toString().trim();
        String productQuantityString = mProductQuantityEditText.getText().toString().trim();
        String productSupplierPhoneNumberString = mProductSupplierPhoneNumberEditText.getText().toString().trim();

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(productPriceString) &&
                TextUtils.isEmpty(productQuantityString) && TextUtils.isEmpty(productSupplierPhoneNumberString) && mSupplieName == InventoryEntry.SUPPLIER_UNKNOWN) {
            return;
        }
        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(productNameString)) {
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        } else {
            Toast.makeText(this, getString(R.string.product_name_requires), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSupplieName == InventoryEntry.SUPPLIER_UNKNOWN) {
            Toast.makeText(this, getString(R.string.supplier_name_requires), Toast.LENGTH_SHORT).show();
        } else {
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, mSupplieName);
        }

        int productPriceInteger = 0;
        if (!TextUtils.isEmpty(productPriceString)) {
            productPriceInteger = Integer.parseInt(productPriceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, productPriceInteger);
        } else {
            Toast.makeText(this, getString(R.string.price_requires), Toast.LENGTH_SHORT).show();
        }

        int productQuantityInteger = 0;
        if (!TextUtils.isEmpty(productQuantityString)) {
            productQuantityInteger = Integer.parseInt(productQuantityString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, productQuantityInteger);
        } else {
            Toast.makeText(this, getString(R.string.quantity_requires), Toast.LENGTH_SHORT).show();
        }


        int productSupplierPhoneNumberInteger = 0;
        if (!TextUtils.isEmpty(productSupplierPhoneNumberString)) {
            productSupplierPhoneNumberInteger = Integer.parseInt(productSupplierPhoneNumberString);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberInteger);
        } else {
            Toast.makeText(this, getString(R.string.supplier_phone_requires), Toast.LENGTH_SHORT).show();
        }

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        Log.d("message", "open Editor Activity");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertProduct();
                finish();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentSupplierName = cursor.getInt(supplierNameColumnIndex);
            int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            mProductNameEditText.setText(currentName);
            mProductPriceEditText.setText(Integer.toString(currentPrice));
            mProductQuantityEditText.setText(Integer.toString(currentQuantity));
            mProductSupplierPhoneNumberEditText.setText(Integer.toString(currentSupplierPhone));

            switch (currentSupplierName) {
                case InventoryEntry.SUPPLIER_AMAZON:
                    mProductSupplieNameSpinner.setSelection(1);
                    break;
                case InventoryEntry.SUPPLIER_JARIRR:
                    mProductSupplieNameSpinner.setSelection(2);
                    break;
                case InventoryEntry.SUPPLIER_OBEIKAN:
                    mProductSupplieNameSpinner.setSelection(3);
                    break;
                default:
                    mProductSupplieNameSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductPriceEditText.setText("");
        mProductQuantityEditText.setText("");
        mProductSupplierPhoneNumberEditText.setText("");
        mProductSupplieNameSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
