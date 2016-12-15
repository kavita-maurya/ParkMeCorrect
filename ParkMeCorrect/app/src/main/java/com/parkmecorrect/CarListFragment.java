package com.parkmecorrect;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CarListFragment extends Fragment {

    private RecyclerView mAlertRecyclerView;
    private CarAdapter mAdapter;
    private Car mCar;
    private int filterType;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        filterType = args.getInt("type");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_car_list, container, false);

        mAlertRecyclerView = (RecyclerView) view
                .findViewById(R.id.car_recycler_view);
        mAlertRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())   {
            case R.id.menu_refresh:
                updateUI();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CarList carList = CarList.getInstance(getActivity());

        List<Car> parkedCars = null;
        if(carList != null) {
            parkedCars = carList.getCars();
        }

        if(filterType==1) {
            List<Car> filter = new ArrayList<Car>();
            for(int i = 0; i < parkedCars.size(); i++){
                Car car = parkedCars.get(i);
                if(car.getType().equalsIgnoreCase("Registered")){
                    filter.add(car);
                }
            }
            parkedCars = filter;
        }
        else if(filterType==2){
            List<Car> filter = new ArrayList<Car>();
            for(int i = 0; i < parkedCars.size(); i++){
                Car car = parkedCars.get(i);
                if(car.getType().equalsIgnoreCase("Guest")){
                    filter.add(car);
                }
            }
            parkedCars = filter;
        }

        //Log.d("Fragment","size"+parkedCars.size());

            mAdapter = new CarAdapter(parkedCars);
            mAlertRecyclerView.setAdapter(mAdapter);


    }

    private class CarHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mCarTextView;
        private TextView mTimeTextView;
        private TextView mTypeTextView;
        private TextView mMobTextView;

        private Car mCar;

        public CarHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mCarTextView = (TextView) itemView.findViewById(R.id.list_item_carno_text_view);
            mTimeTextView = (TextView) itemView.findViewById(R.id.list_item_checkIn_text_view);
            mTypeTextView= (TextView) itemView.findViewById(R.id.list_item_carType_text_view);
            mMobTextView= (TextView) itemView.findViewById(R.id.list_item_car_mob_text_view);
        }

        public void bindCar(Car car) {
            mCar = car;
            mCarTextView.setText("Car No: "+car.getCarNo());
            mTimeTextView.setText("CheckIn Time: "+car.getCheckIn());
            mTypeTextView.setText("Car Type: "+car.getType());
            mMobTextView.setText("Mob No: "+car.getMob());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(),
                    mCar.getCarNo() + " clicked!", Toast.LENGTH_SHORT)
                    .show();

            //Intent intent = new Intent(getActivity(), CrimeActivity.class);

            /*Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);*/
        }
    }

    private class CarAdapter extends RecyclerView.Adapter<CarHolder> {

        private List<Car> parkedCars;

        public CarAdapter(List<Car> pCars) {

            if(pCars != null) {
                parkedCars = pCars;
                //Toast.makeText(getActivity(),"Total parked vehicles "+getItemCount(),Toast.LENGTH_SHORT).show();
            } else {
                parkedCars = new ArrayList<Car>();
                Toast.makeText(getActivity(),R.string.errorMessage,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public CarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_car, parent, false);
            return new CarHolder(view);
        }

        @Override
        public void onBindViewHolder(CarHolder holder, int position) {
            Car car = parkedCars.get(position);
            holder.bindCar(car);
        }

        @Override
        public int getItemCount() {
            return parkedCars.size();
        }
    }
}
