package razon.contactlist.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import razon.contactlist.Data.DatabaseHandler;
import razon.contactlist.Model.Contact;
import razon.contactlist.R;

/**
 * Created by razon30 on 31-03-17.
 */

public class AdapterContact extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Contact> contactList = new ArrayList<Contact>();
    Context context;

    // Value to show different view
    private final int CONTACTS = 0;
    private final int LOAD_MORE = 1;

    //offset to keep track of pages
    int offset = 1;

    //setting ranges from sqlite database
    int lowerRange = 0;
    int upperRange = 9;

    //database instance
    DatabaseHandler databaseHandler;


    //Constructor
    public AdapterContact(Context context) {
        this.context = context;

        // database object
        databaseHandler = new DatabaseHandler(context);

        //clearing activity class for the first time
        contactList.clear();

        //getting limit to check data limit in sqlite database
        int limit = offset * 10;

        //checking whether limit crosses datalist or not
        if (databaseHandler.getContactsCount() < limit) {

            //if crosses , setting datalist size to upperrange
            upperRange = databaseHandler.getContactsCount();
        }

        //method to fetch data from database and setting to contact list
        addData(upperRange, lowerRange);

    }


    //method to fetch data from database and setting to contact list
    public void addData(int upperRange, int lowerRange) {

        //getting data from database and setting to cantactList
        contactList.addAll(databaseHandler.getTenContact(lowerRange, upperRange));

        //notifying adapter to change the dataset
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        //checking view position crosses contactlist
        if (position >= contactList.size()) {
            //if crosses, returns value to inflates Load More view
            return LOAD_MORE;
        } else {

            //if not, returns value to inflates contacts item view
            return CONTACTS;
        }
    }

    @Override
    public int getItemCount() {

        //adding one more view position to add load more view in the bottom
        return contactList.size()+1;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case CONTACTS:

                // inflating contact item
                View v1 = inflater.inflate(R.layout.layout_contact_item, parent, false);

                //getting contact viewholder
                viewHolder = new ViewHolderContacts(v1);
                break;
            case LOAD_MORE:

                //// inflating load more view
                View v2 = inflater.inflate(R.layout.layout_footer_item, parent, false);

                //getting load more viewholder
                viewHolder = new ViewHolderFooter(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case CONTACTS:

                //Binding contacts viewholder
                ViewHolderContacts vh1 = (ViewHolderContacts) holder;

                //operating on contact view
                configureViewHolder1(vh1, position);
                break;
            case LOAD_MORE:

                // //Binding contacts viewholder
                ViewHolderFooter vh2 = (ViewHolderFooter) holder;

                // //operating on load more view
                configureViewHolder2(vh2, position);
                break;
        }


    }

    private void configureViewHolder1(ViewHolderContacts vh1, int position) {

        //getting current contact object
        Contact currentContact = contactList.get(position);

        //settign name to view
        vh1.tvName.setText(currentContact.getName());

        //settign number to view
        vh1.tvNumber.setText(currentContact.getNumber());

        Log.d("sms", position + "");


    }


    private void configureViewHolder2(final ViewHolderFooter vh2, int position) {

        //clicking on load more
        vh2.tvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //increasing offset with each click
                offset++;

                //setting limit
                int limit = offset * 10;

                //setting lower range
                lowerRange = upperRange+1;

                //setting upper range
                upperRange = upperRange+10;

                //checking whether limit crosses datalist or not
                if (databaseHandler.getContactsCount() <= limit) {

                    //if crosses
                    //setting datalist size to upperrange
                    upperRange = databaseHandler.getContactsCount();

                    //setting no more data to show text
                    vh2.tvLoadMore.setText("No more results to show");

                    //disabling load more button
                    vh2.tvLoadMore.setEnabled(false);
                }

                //method to fetch data from database and setting to contact list
                addData(upperRange, lowerRange);


            }
        });

    }


    //viewholder for contacts
    public class ViewHolderContacts extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvNumber;

        public ViewHolderContacts(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvNumber = (TextView) itemView.findViewById(R.id.tvNumber);

        }
    }

    //view holder footer
    public class ViewHolderFooter extends RecyclerView.ViewHolder {

        TextView tvLoadMore;

        public ViewHolderFooter(View itemView) {
            super(itemView);

            tvLoadMore = (TextView) itemView.findViewById(R.id.tvLoadMore);

        }
    }

}
