package kryternext.graduatework.app.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import kryternext.graduatework.R;
import kryternext.graduatework.app.models.Product;

public class GoodsListAdapter extends BaseAdapter {
    private Context context;
    private TextView totalTV;
    private List<Product> products;
    private static LayoutInflater inflater = null;
    private Map<String, String> orderProductList;

    public GoodsListAdapter(Context context, List<Product> products, TextView totalTV, Map<String, String> orderProductList) {
        this.context = context;
        this.products = products;
        this.orderProductList = orderProductList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.totalTV = totalTV;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View productRow = convertView;
        final Product product = products.get(position);
        if (productRow == null)
            productRow = inflater.inflate(R.layout.product_row, parent, false);
        // TextView totalPriceTV = (TextView) ;
        TextView nameTV = (TextView) productRow.findViewById(R.id.productName);
        nameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                alertDialog.setTitle(product.getProductName());
                alertDialog.setMessage(product.getProductDescription());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
        TextView priceTV = (TextView) productRow.findViewById(R.id.productPrice);
        final TextView countTV = (TextView) productRow.findViewById(R.id.productCount);
        final TextView selectedCountTV = (TextView) productRow.findViewById(R.id.productSelectedCount);
        Button minus = (Button) productRow.findViewById(R.id.minusProduct);
        Button plus = (Button) productRow.findViewById(R.id.plusProduct);
        nameTV.setText(String.format(Locale.ENGLISH, "%d: %s", (position + 1), product.getProductName()));
        priceTV.setText(String.format(Locale.ENGLISH, "%.2f$", product.getPrice()));
        countTV.setText(String.format(Locale.ENGLISH, "%d", product.getCount()));
        selectedCountTV.setText("0");
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countStr = selectedCountTV.getText().toString();
                String totalCountStr = totalTV.getText().toString().split(":")[1].trim();
                double totalCount = Double.parseDouble(totalCountStr);
                int productCount = Integer.parseInt(countTV.getText().toString());
                int selectedCount = Integer.parseInt(countStr);
                if (selectedCount > 0) {
                    countTV.setText(String.valueOf(++productCount));
                    selectedCountTV.setText(String.valueOf(--selectedCount));
                    totalCount -= product.getPrice();
                    totalTV.setText(String.format(Locale.ENGLISH, "Total: %.2f", totalCount));
                    if (selectedCount != 0) {
                        orderProductList.put(product.getProductName(), String.format(Locale.ENGLISH, "%d-%d", selectedCount, productCount));
                    } else {
                        orderProductList.remove(product.getProductName());
                    }
                } else {
                    Snackbar.make(view, "Count is 0", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countStr = selectedCountTV.getText().toString();
                String totalCountStr = totalTV.getText().toString().split(":")[1].trim();
                double totalCount = Double.parseDouble(totalCountStr);
                int productCount = Integer.parseInt(countTV.getText().toString());
                int selectedCount = Integer.parseInt(countStr);
                if (productCount > 0) {
                    countTV.setText(String.valueOf(--productCount));
                    selectedCountTV.setText(String.valueOf(++selectedCount));
                    totalCount += product.getPrice();
                    totalTV.setText(String.format(Locale.ENGLISH, "Total: %.2f", totalCount));
                    orderProductList.put(product.getProductName(), String.format(Locale.ENGLISH, "%d-%d", selectedCount, productCount));
                } else {
                    Snackbar.make(view, "Maximal product count reached!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
        });
        return productRow;
    }
}
