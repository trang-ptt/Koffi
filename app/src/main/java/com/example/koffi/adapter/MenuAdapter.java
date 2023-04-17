package com.example.koffi.adapter;

import static com.example.koffi.FunctionClass.setListViewHeight;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koffi.R;
import com.example.koffi.models.CartItem;
import com.example.koffi.models.Category;
import com.example.koffi.models.Item;
import com.example.koffi.models.Topping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
//import com.makeramen.roundedimageview.RoundedImageView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> implements Filterable {

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String search = charSequence.toString().toLowerCase(Locale.ROOT);
                if (search.isEmpty()) {
                    menuArray = menuArrayOld;
                }
                else {
                    ArrayList<Category> list = new ArrayList<>();
                    for (Category category : menuArrayOld) {
                        ArrayList<Item> itemsArray = new ArrayList<Item>();
                        for (Item item : category.items) {
                            if (item.name.toLowerCase(Locale.ROOT).contains(search)) {
                                Item menuItem = new Item(item.id, item.name, item.image, item.price, item.description);
                                itemsArray.add(menuItem);
                            }
                        }
                        Category categoryNew = new Category(category.id, category.name, category.image, itemsArray);
                        list.add(categoryNew);
                        menuArray = list;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = menuArray;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                menuArray = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        ListView listView;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            listView = itemView.findViewById(R.id.menuList);
            text = itemView.findViewById(R.id.categoryName);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Assign data
                    Item item = (Item) listView.getItemAtPosition(i);
                    openBottomSheet(item,view);
                }
            });
        }

    }
    long total;
    boolean isL = false;
    long unit;
    long sizePrice = 0;
    int number;
    String size;
    String note;
    EditText edtNote;

    public void openBottomSheet(Item item, View view) {
        //Bottom Sheet Dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext(),R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(view.getContext()).inflate(R.layout.bottomsheet_itemdetail,
                (LinearLayout)view.findViewById(R.id.menu_bottomsheet));
        bottomSheetDialog.setContentView(bottomSheetView);

        //Topping ListView
        ListView toppingListView = bottomSheetView.findViewById(R.id.topping_listview);

        ArrayList<Topping> toppingArray = new ArrayList<Topping>();

        ToppingAdapter toppingAdapter = new ToppingAdapter(context, toppingArray);
        toppingListView.setAdapter(toppingAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("toppings").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Topping topping = new Topping(documentSnapshot.getId(), documentSnapshot.getString("name"),
                                    documentSnapshot.getLong("price"));
                            toppingArray.add(topping);
                        }
                        toppingAdapter.notifyDataSetChanged();
                        setListViewHeight(toppingListView);
                    }
                });

        //Init
        ImageView imageView = bottomSheetView.findViewById(R.id.itemdetail_image);
        TextView tvName = bottomSheetView.findViewById(R.id.tvName);
        TextView tvPrice = bottomSheetView.findViewById(R.id.tvPrice);
        TextView tvDes = bottomSheetView.findViewById(R.id.tvDescription);
        ImageButton closeView = bottomSheetDialog.findViewById(R.id.itemdetail_closeBtn);
        TextView tvNumber = bottomSheetDialog.findViewById(R.id.tvNumber);
        Button totalBtn = bottomSheetView.findViewById(R.id.itemTotalPrice);
        ImageButton plusBtn = bottomSheetDialog.findViewById(R.id.plusButton);
        ImageButton minusBtn = bottomSheetDialog.findViewById(R.id.minusButton);
        RadioButton sizeM = bottomSheetView.findViewById(R.id.sizeM_radio);
        RadioButton sizeL = bottomSheetView.findViewById(R.id.sizeL_radio);
        edtNote = bottomSheetDialog.findViewById(R.id.edtNote);
        ArrayList<Topping> toppingToCart = new ArrayList<>();

        //Handle bottom sheet
        tvName.setText(item.name);
        int drawableId = view.getResources().getIdentifier(item.image, "drawable", context.getPackageName());
        imageView.setImageResource(drawableId);
        tvPrice.setText(item.price + "đ");
        tvDes.setText(item.description);
        tvNumber.setText("1");
        totalBtn.setEnabled(false);

        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        number = Integer.parseInt(tvNumber.getText().toString());
        tvNumber.setText(Long.toString(number));
        unit = item.price;
        totalBtn.setText(Long.toString(unit)+"đ");


        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = Integer.parseInt(tvNumber.getText().toString()) + 1;
                tvNumber.setText(Long.toString(number));
                checkListViewCheckBox(toppingListView, toppingArray, bottomSheetView, number);
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Long.parseLong(tvNumber.getText().toString()) - 1 > 0)
                    number = Integer.parseInt(tvNumber.getText().toString()) - 1;
                if (number > 0) {
                    tvNumber.setText(Long.toString(number));
                    checkListViewCheckBox(toppingListView, toppingArray, bottomSheetView, number);
                }
            }
        });

        sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sizeL.setChecked(false);
                    isL = false;
                }
                if (isL) {
                    sizePrice = 6000;
                }
                else {
                    sizePrice = 0;
                }
                checkListViewCheckBox(toppingListView, toppingArray, bottomSheetView, number);
                totalBtn.setEnabled(true);
            }
        });
        sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sizeM.setChecked(false);
                    isL = true;
                }
                if (isL) {
                    sizePrice = 6000;
                }
                else {
                    sizePrice = 0;
                }
                checkListViewCheckBox(toppingListView, toppingArray, bottomSheetView, number);
                totalBtn.setEnabled(true);
            }
        });

        toppingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkBox = toppingListView.getChildAt(i).findViewById(R.id.checkBox);
                checkBox.setChecked(!checkBox.isChecked());
                checkListViewCheckBox(toppingListView, toppingArray, bottomSheetView, number);
            }
        });

        totalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(bottomSheetDialog.getContext(), "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_SHORT).show();
                } else {
                    Query query = db.collection("order")
                            .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .whereEqualTo("status", 0);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String totalTxt = totalBtn.getText().toString();
                                total = Long.parseLong(totalTxt.substring(0, totalTxt.length() - 1));
                                size = sizeL.isChecked() ? "Upsize" : "Vừa";
                                for (int i = 0; i < 8; i++) {
                                    CheckBox checkBox = toppingListView.getChildAt(i).findViewById(R.id.checkBox);
                                    if (checkBox.isChecked()) {
                                        toppingToCart.add(toppingArray.get(i));
                                    }
                                }
                                addItemToCart(db, item.name, toppingToCart);
                                bottomSheetDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

        //Dismiss listener
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                sizeM.setChecked(false);
                sizeL.setChecked(false);

                for (int n = 0; n < 8; n++) {
                    CheckBox checkBox = toppingListView.getChildAt(n).findViewById(R.id.checkBox);
                    checkBox.setChecked(false);
                }
            }
        });

        //Show dialog
        bottomSheetDialog.show();
    }
    private void checkListViewCheckBox(ListView toppingListView, ArrayList<Topping> toppingArray, View bottomSheetView, long number) {
        long sum = 0;
        for (int n = 0; n < 8; n++) {
            CheckBox itemCheckBox = toppingListView.getChildAt(n).findViewById(R.id.checkBox);
            if (itemCheckBox.isChecked()) {
                sum += toppingArray.get(n).price;
                System.out.println("result: " + sum);
            }
        }
        Button totalBtn = bottomSheetView.findViewById(R.id.itemTotalPrice);
        totalBtn.setText(Long.toString((unit + sum + sizePrice) * number)+"đ");
    }

    private void addItemToCart(FirebaseFirestore db, String itemName, ArrayList<Topping> toppingArray) {
        note = edtNote.getText().toString().trim();
        Query query = db.collection("order")
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status", 0);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        db.collection("cartItems")
                                .whereEqualTo("cartID", doc.getId())
                                .whereEqualTo("item", itemName).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() == 0) {
                                                CartItem cartItem = new CartItem(doc.getId(), itemName, number, total, size, toppingArray, note);
                                                db.collection("cartItems").add(cartItem);
                                            }
                                            else {
                                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                    CartItem result = snapshot.toObject(CartItem.class);
                                                    //Convert toppings to string
                                                    String arrayFromDb = result.size;
                                                    String current = size;
                                                    for (Topping topping : toppingArray) {
                                                        current += topping.id;
                                                    }
                                                    for (Topping topping : result.toppings) {
                                                        arrayFromDb += topping.id;
                                                    }
                                                    System.out.println("Topping array: " + current);
                                                    System.out.println("Topping array from db: " + arrayFromDb);

                                                    if (current.equals(arrayFromDb)) {
                                                        long newQuatity = result.quantity + number;
                                                        long newPrice = result.price + total;
                                                        db.collection("cartItems")
                                                                .document(snapshot.getId())
                                                                .update("quantity", newQuatity, "price", newPrice);
                                                        break;
                                                    } else {
                                                        CartItem cartItem = new CartItem(doc.getId(), itemName, number, total, size, toppingArray, note);
                                                        db.collection("cartItems").add(cartItem);
                                                        break;
                                                    }
                                                }
                                             }
                                        }
                                        Toast.makeText(context.getApplicationContext(), "Thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
    }

    Context context;
    ArrayList<Category> menuArray;
    ArrayList<Category> menuArrayOld;

    public MenuAdapter(Context context, ArrayList<Category> menuArray) {
        this.context = context;
        this.menuArray = menuArray;
        this.menuArrayOld = menuArray;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.recycler_menu,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = menuArray.get(position);

        holder.text.setText(category.name);

        MenuItemAdapter itemAdapter = new MenuItemAdapter(context,menuArray.get(position).items);
        holder.listView.setAdapter(itemAdapter);
        setListViewHeight(holder.listView);
    }

    @Override
    public int getItemCount() {
        return menuArray.size();
    }
}
