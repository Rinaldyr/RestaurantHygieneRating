package com.example.rinaldy.restauranthygienechecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListViewFragment extends Fragment {

    private Context mContext;
    private SharedPreferences sharedPreferences;
    private ArrayList<Establishment> mEstablishments = new ArrayList<>();
    private ArrayList<Establishment> mEstablishmentsCopy = new ArrayList<>();
    private OnePage.Meta mMeta;
    private ListView mListView;
    private CustomViewAdapter mListViewAdapter;
    private String sortBy = "";
    private String listOrder = "Asc";
    private int pageNumber = 1;

    ViewSwitcher viewSwitcher;
    ImageButton switchNext;
    ImageButton submitSort;
    SearchView searchBar;
    MarqueeTextView status;
    Spinner sortOption;
    Spinner sortOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        final View rootView = localInflater.inflate(R.layout.fragment_list_view, container, false);

        mContext = rootView.getContext();
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.view_switcher);
        switchNext = (ImageButton) rootView.findViewById(R.id.switch_view);
        submitSort = (ImageButton) rootView.findViewById(R.id.submit_sort);
        searchBar = (SearchView) rootView.findViewById(R.id.search_list);
        status = (MarqueeTextView) rootView.findViewById(R.id.list_status);
        sortOption = (Spinner) rootView.findViewById(R.id.sort_option);
        sortOrder = (Spinner) rootView.findViewById(R.id.sort_order);
        pageNumber = sharedPreferences.getInt("pageNumber", 1);

        mListViewAdapter = new CustomViewAdapter(mContext, mEstablishments);
        mListView = (ListView) rootView.findViewById(R.id.establishmentList);
        mListView.setOnItemClickListener(itemClickListener);
        mListView.setAdapter(mListViewAdapter);

        String statusMessage = mMeta.getTotalCount() > 0 ? ("There are " + mMeta.getTotalCount() + " establishments found. Page " + pageNumber + " of " + mMeta.getTotalPages() + ".") : "No results found.";
        status.setText(statusMessage);

        if (!mListViewAdapter.isEmpty()) {
            View footerView = localInflater.inflate(R.layout.list_view_footer_layout, null, false);
            ((ImageButton) footerView.findViewById(R.id.navigation_left)).setOnClickListener(leftClick);
            ((ImageButton) footerView.findViewById(R.id.navigation_right)).setOnClickListener(rightClick);
            ((ImageButton) footerView.findViewById(R.id.navigation_first_page)).setOnClickListener(firstPageClick);
            ((ImageButton) footerView.findViewById(R.id.navigation_last_page)).setOnClickListener(lastPageClick);
            mListView.addFooterView(footerView);
        }

        mEstablishmentsCopy.addAll(mEstablishments);
        ViewCompat.setNestedScrollingEnabled(mListView, true);

        viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_up));
        viewSwitcher.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_down));
        switchNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.showNext();
            }
        });
        searchBar.setQueryHint("Type something to filter");
        searchBar.setOnQueryTextListener(searchBarListener);
        submitSort.setOnClickListener(sortListener);

        mListViewAdapter.notifyDataSetChanged();
        return rootView;
    }

    public void initialiseList(ArrayList<Establishment> establishments, OnePage.Meta meta) {
        this.mEstablishments = establishments;
        this.mMeta = meta;
    }

    final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Establishment clickedItem = mListViewAdapter.getItem(i);
            Intent intent = new Intent(getActivity(), EstablishmentDetail.class);
            intent.putExtra("establishmentID", clickedItem.getFHRSID());
            startActivity(intent);
        }
    };

    public void filterBySearch(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            Log.i("DBG", "List : " + mEstablishmentsCopy);
            mListViewAdapter.clear();
            mListViewAdapter.addAll(mEstablishmentsCopy);
            mListViewAdapter.notifyDataSetChanged();
        } else {
            ArrayList<Establishment> filteredList = new ArrayList<>();
            prefix = prefix.toLowerCase();
            for (Establishment e : mEstablishments) {
                if (e.getBusinessName().toLowerCase().startsWith(prefix)) {
                    filteredList.add(e);
                }
            }

            Log.i("DEBUG", filteredList.toString());
            mListViewAdapter.clear();
            mListViewAdapter.addAll(filteredList);
            mListViewAdapter.notifyDataSetChanged();
        }
    }

    final SearchView.OnQueryTextListener searchBarListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            filterBySearch(s);
            return true;
        }
    };

    public void sort(String option, String order) {
        Comparator comparator;
        switch (option) {
            case "Distance":
                comparator = new Comparator<Establishment>() {
                    @Override
                    public int compare(Establishment e1, Establishment e2) {
                        if (e1.getDistance() == null || e2.getDistance() == null) return 0;
                        if (e1.getDistance() > e2.getDistance()) return 1;
                        if (e1.getDistance() < e2.getDistance()) return -1;
                        return 0;
                    }
                };
                break;
            case "Rating":
                comparator = new Comparator<Establishment>() {
                    @Override
                    public int compare(Establishment e1, Establishment e2) {
                        return e1.getRatingValue().compareTo(e2.getRatingValue());
                    }
                };
                break;
            case "Date Awarded":
                comparator = new Comparator<Establishment>() {
                    @Override
                    public int compare(Establishment e1, Establishment e2) {
                        if (e1.getRatingDate().after(e2.getRatingDate())) return 1;
                        if (e1.getRatingDate().before(e2.getRatingDate())) return -1;
                        else return 0;
                    }
                };
                break;
            case "Name":
            default:
                comparator = new Comparator<Establishment>() {
                    @Override
                    public int compare(Establishment e1, Establishment e2) {
                        return e1.getBusinessName().compareTo(e2.getBusinessName());
                    }
                };
                break;
        }

        mEstablishments.clear();
        mEstablishments.addAll(mEstablishmentsCopy);
        Collections.sort(mEstablishments, comparator);
        if (order.equals("Desc")) {
            Collections.reverse(mEstablishments);
        }
        mListViewAdapter.notifyDataSetChanged();
    }

    final View.OnClickListener sortListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String option = sortOption.getSelectedItem().toString();
            String order = sortOrder.getSelectedItem().toString();

            if (!sortBy.equals(option) || !listOrder.equals(order)) {
                sort(option, order);
                sortBy = option;
                listOrder = order;
            }
        }
    };

    final View.OnClickListener leftClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (pageNumber > 1) {
                pageNumber -= 1;
                sharedPreferences.edit().putInt("pageNumber", pageNumber).apply();
                Intent intent = new Intent(getActivity(), ViewActivity.class);
                intent.putExtra("search", sharedPreferences.getString("search", null));
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "It's the first page", Toast.LENGTH_SHORT).show();
            }
        }
    };

    final View.OnClickListener rightClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (pageNumber < mMeta.getTotalPages()) {
                pageNumber += 1;
                sharedPreferences.edit().putInt("pageNumber", pageNumber).apply();
                Intent intent = new Intent(getActivity(), ViewActivity.class);
                intent.putExtra("search", sharedPreferences.getString("search", null));
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "It's the last page", Toast.LENGTH_SHORT).show();
            }
        }
    };

    final View.OnClickListener firstPageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (pageNumber > 1) {
                pageNumber = 1;
                sharedPreferences.edit().putInt("pageNumber", pageNumber).apply();
                Intent intent = new Intent(getActivity(), ViewActivity.class);
                intent.putExtra("search", sharedPreferences.getString("search", null));
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "It's the first page", Toast.LENGTH_SHORT).show();
            }
        }
    };

    final View.OnClickListener lastPageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (pageNumber < mMeta.getTotalPages()) {
                pageNumber = mMeta.getTotalPages();
                sharedPreferences.edit().putInt("pageNumber", pageNumber).apply();
                Intent intent = new Intent(getActivity(), ViewActivity.class);
                intent.putExtra("search", sharedPreferences.getString("search", null));
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "It's the last page", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
