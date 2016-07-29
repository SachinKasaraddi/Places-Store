package com.sachi.placesstore.db;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sachin.kasaraddi on 28/07/16.
 */
public class TravelStore {
    private Realm realm;

    public TravelStore(Context context) {
        realm = ProtoRealm.getInstance(context).getRealm();
    }

    public static TravelStore getInstance(Context context) {
        return new TravelStore(context);
    }

    public void writeMessage(final Travel travel) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm1) {
                Log.d("Writing to DB",travel.getTitle());
                realm1.copyToRealm(travel);
            }
        });
    }

    public RealmResults<Travel> getAllTravels() {
        return realm.where(Travel.class).findAll();

    }

    public void refresh() {
        if (realm != null) {
            realm.refresh();
        }
    }

    public Travel getAllTravelRoutes(String title) {
        return realm.where(Travel.class).contains("title", title).findFirst();
    }

    public boolean isLifeHackExist(String title) {
        RealmQuery<Travel> query = realm.where(Travel.class)
                .equalTo("title", title);
        return query.count() > 0;
    }

    public boolean isTravelsExist() {
        RealmQuery<Travel> query = realm.where(Travel.class);
        return query.count() > 0;

    }

    public void close() {
        realm.close();
    }
}
