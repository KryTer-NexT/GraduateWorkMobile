package kryternext.graduatework.app.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.auth.Auth;
import com.mongodb.stitch.android.auth.AvailableAuthProviders;
import com.mongodb.stitch.android.auth.anonymous.AnonymousAuthProvider;
import com.mongodb.stitch.android.services.mongodb.MongoClient;
import com.mongodb.stitch.android.services.mongodb.MongoClient.Collection;
import com.mongodb.stitch.android.services.mongodb.MongoClient.Database;
import com.mongodb.stitch.android.StitchClient;

import org.bson.Document;

import java.util.List;

/**
 * This class controller used for connect to database, getting, saving data from it.
 */
public class DatabaseController implements MongoDB {
    private static final String APP_ID = "wms-bjlte"; //The Stitch Application ID
    private static final String TAG = "STITCH-SDK";
    private static final String MONGODB_SERVICE_NAME = "mongodb-atlas";

    private Database database;
    private StitchClient client;

    public DatabaseController(Context context, String databaseName) {
        client = new StitchClient(context, APP_ID);
        MongoClient mongoClient = new MongoClient(client, MONGODB_SERVICE_NAME);
        this.database = mongoClient.getDatabase(databaseName);
        doAnonymousAuthentication();
    }

    private void doAnonymousAuthentication() {
        client.getAuthProviders().addOnCompleteListener(new OnCompleteListener<AvailableAuthProviders>() {
            @Override
            public void onComplete(@NonNull final Task<AvailableAuthProviders> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Could not retrieve authentication providers");
                } else {
                    Log.i(TAG, "Retrieved authentication providers");
                    if (task.getResult().hasAnonymous()) {
                        client.logInWithProvider(new AnonymousAuthProvider()).continueWith(new Continuation<Auth, Object>() {
                            @Override
                            public Object then(@NonNull final Task<Auth> task) throws Exception {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "User Authenticated as " + client.getAuth().getUserId());
                                } else {
                                    Log.e(TAG, "Error logging in anonymously", task.getException());
                                }
                                return null;
                            }
                        });
                    }
                }
            }
        });
    }

    public void insert(String collection, final Document newDocument) {
        Log.i(TAG, "Try to insert!");
        if (!client.isAuthenticated()) {
            Log.e(TAG, "Account didn't authorized!");
        } else {
            Log.i(TAG, "New insert document: " + newDocument.toString());
            this.database.getCollection(collection).insertOne(newDocument).continueWith(
                    new Continuation<Void, List<Document>>() {
                        @Override
                        public List<Document> then(@NonNull Task<Void> task) throws Exception {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Document added!");
                            } else {
                                Log.e(TAG, "Error inserting document");
                            }
                            return null;
                        }
                    });
        }
    }

    public void save(String collection, Document query, Document updDocument) {
        Document set = new Document("$set", updDocument);
        this.database.getCollection(collection).updateOne(query, set);
    }

    public Task<Integer> getCountByQuery(String collection, Document query) {
        return this.database.getCollection(collection).count(query);
    }

    public Task<List<Document>> getListByQuery(String collection, Document query) {
        return this.database.getCollection(collection).find(query);
    }

    public Collection getCollection(String collection) {
        return this.database.getCollection(collection);
    }
}
