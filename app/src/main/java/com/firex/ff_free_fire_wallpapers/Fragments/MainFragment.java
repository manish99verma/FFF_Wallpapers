package com.firex.ff_free_fire_wallpapers.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firex.ff_free_fire_wallpapers.Adapters.CatAdapter;
import com.firex.ff_free_fire_wallpapers.Adapters.CatAdapter2;
import com.firex.ff_free_fire_wallpapers.Adapters.PopAdapter;
import com.firex.ff_free_fire_wallpapers.BuildConfig;
import com.firex.ff_free_fire_wallpapers.Dialogs.CustomReviewManager;
import com.firex.ff_free_fire_wallpapers.Models.CatModel;
import com.firex.ff_free_fire_wallpapers.Models.WallpaperModel;
import com.firex.ff_free_fire_wallpapers.R;
import com.firex.ff_free_fire_wallpapers.Utilities.PrefManager;
import com.firex.ff_free_fire_wallpapers.Utilities.Utils;
import com.firex.ff_free_fire_wallpapers.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.review.ReviewManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFragment extends Fragment {
    ArrayList<String> smallUrls = new ArrayList<>();
    ArrayList<String> smallUrlsMore = new ArrayList<>();
    ArrayList<String> bigUrls = new ArrayList<>();
    ArrayList<String> bigUrlsMore = new ArrayList<>();
    private final String ver = "1";
    private int currentDatabaseSession;
    private int databaseUpdateSession;

    FirebaseFirestore db;
    private final String TAG = "HomeFragmentTAGG";
    public Context context;

    private SharedPreferences prefs;
    CatAdapter categoryAdapter;
    CatAdapter2 categoryAdapter2;
    private DatabaseReference refUrl;
    Activity activity;
    FragmentHomeBinding binding;
    Executor executor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        context = binding.getRoot().getContext();
        activity = (Activity) context;

        ((AppCompatActivity) activity).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);

        executor = ContextCompat.getMainExecutor(context);

        db = FirebaseFirestore.getInstance();
        refUrl = FirebaseDatabase.getInstance().getReference("Urls");

        prefs = context.getSharedPreferences("HomeFragmentPref", Context.MODE_PRIVATE);

        currentDatabaseSession = prefs.getInt("currentDbSession", 0);
        databaseUpdateSession = prefs.getInt("dbUpdateSession_xyz", 10);

        prefs.edit().putInt("currentDbSession", ++currentDatabaseSession).apply();
        Log.i("DBUpdateSession", "onCreateView: dbUpdateSession: " + databaseUpdateSession);
        updateDatabaseSession();

        getCategoriesData();
        binding.RVCategories.setNestedScrollingEnabled(false);
        binding.RVMore.setNestedScrollingEnabled(false);

        new CustomReviewManager(context).showIfNotRated();

        return binding.getRoot();
    }

//    private void getTrendingData(Source source) {
//        if (source == Source.SERVER) {
//            Log.i(TAG, "getTrendingData : form server!");
//        } else {
//            Log.i(TAG, "getTrendingData: form caches!");
//        }
//
//        //Trending Images
//        db.collection("All_Urls_101")
//                .orderBy("popularity", Query.Direction.ASCENDING)
//                .limit(50)
//                .get(source).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
////                List<Wallpaper> data = task.getResult().toObjects(Wallpaper.class);
////                ArrayList<Wallpaper> listTrending = new ArrayList<>(data);
//
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Iterator<QueryDocumentSnapshot> iterator = task.getResult().iterator();
//                        ArrayList<Wallpaper> list = new ArrayList<>();
//                        int count = 0;
//                        while (iterator.hasNext()) {
//                            count++;
//                            QueryDocumentSnapshot snapshot = iterator.next();
//                            Wallpaper wallpaper = new Wallpaper(
//                                    snapshot.getString("sUrl")
//                                    , snapshot.getString("mUrl")
//                                    , snapshot.getString("id")
//                                    , snapshot.getLong("popularity"));
//                            list.add(wallpaper);
//                        }
//
//                        final ArrayList<Wallpaper> finalList = Utility.randomizeArraylist(list);
//                        final int finalCount = count;
//                        main.execute(() -> {
//                            if (finalCount == 0) {
//                                Log.i(TAG, "run: unable to get trending data! Retying");
//                                getTrendingData(Source.SERVER);
//                                return;
//                            } else {
//                                Log.i(TAG, "run: got data trending: " + finalCount);
//                            }
//
//                            PopularAdapter adapter = new PopularAdapter(context, finalList);
//                            binding.RVTrending.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                            binding.PBTrending.setVisibility(View.INVISIBLE);
//                            binding.RVTrending.setAdapter(adapter);
//                        });
//
//                    }
//                }.start();
//
//
//            }
//
//        });
//    }

    private void getCategoriesData() {
//        if (source == Source.SERVER) {
//            Log.i(TAG, "getCategories : form server!");
//        } else {
//            Log.i(TAG, "getCategories: form caches!");
//        }
//
//        //Categories Images
//        db.collection("Categories_101")
//                .orderBy("index")
//                .get(source).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
////                List<Category> data = task.getResult().toObjects(Category.class);
////                ArrayList<Category> listCategories = new ArrayList<>(data);
//
//
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Iterator<QueryDocumentSnapshot> iterator = task.getResult().iterator();
//                        ArrayList<Category> list = new ArrayList<>();
//                        int count = 0;
//                        while (iterator.hasNext()) {
//                            count++;
//                            QueryDocumentSnapshot snapshot = iterator.next();
//                            Category category = new Category(
//                                    snapshot.getString("title")
//                                    , snapshot.getString("url"));
//                            list.add(category);
//
//                        }
//
//                        final int finalCount = count;
//                        main.execute(() -> {
//                            if (finalCount == 0) {
//                                Log.i(TAG, "run: unable to get Categories data! Retying");
//                                getCategoriesData(Source.SERVER);
//                                return;
//                            } else {
//                                Log.i(TAG, "run: got data Categories: " + finalCount);
//                            }
//
//                            categoriesAdapter = new CategoriesAdapter(context, list);
//                            binding.RVCategories.setLayoutManager(new GridLayoutManager(context, 2));
//                            binding.PBCategories.setVisibility(View.INVISIBLE);
//                            binding.RVCategories.setAdapter(categoriesAdapter);
//
//                            new Thread(() -> {
//                                readUrlsFile(false);
//                                readUrlsFile(true);
//                            }).start();
//                        });
//
//
//                    }
//                }.start();
//
//            }
//
//        });
        ArrayList<CatModel> list = new ArrayList<>();

        list.add(new CatModel("Girl", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEi68l5iL4IGEKbc9S0yBqgvDF1u-1voqPE6T2BGId2mUQroUQwDcSAMn5vqMmjIPG7Wi8kQQOicw9uuSLvTIfd7Bj0-WMr8A3wnOtgFPCPq-a3IE5H-Mbf4fIIQVYrKwgd1-ZX1bZ_SNEfDnWhq2gYzeGcQQF2-UkBmideGW7wW2djz43ABEEekP-77/s600/girl.jpg"));
        list.add(new CatModel("Weapon", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgoXsn-Q59XO3FJ0rw9XhI-Y9Mox-hP-epQPJpnhGegVm1kpuq23DKalqdZ-wO2DWCRLrY0bCVgvzQc7o0pjNEw2Y9ANfKUa3vEAoEKAzujhPdr9d4Ce3fhEP997e3m3o9cWrfKGt-tyqwJQxy2XCpIzInPV549f2cfSEHT_VC7bSoLpz70Fg1xcQWc/s600/wepons.jpg"));
        list.add(new CatModel("Vehicle", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjfDarMZVptzLbXKghLaaW4jC5KViWrSPGywIk-n68Z8bSAyWSZrRrfj9udhiFmqpUgMR8EIjqx4liM7Ua0kgJDUK-xrA1Aq83IUsXvpylFi6PXsIYv9BENdVnNIByxX94hda_dXYVv6pbnSTXnAg_-amCFY7BS-FlQKYsROivRRTl1SNx_htLODycF/s600/vechicle.jpg"));
        list.add(new CatModel("Outfit", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiHTGTvbpJ4K_4YYJZTYW-gNfR24OfEG1Bh0P4L9THMhjfhkEXTGiicpEbmoCoUEVer79BPPDgf9z5ArEAkI6p-ye2z0X_iLwtSrGTQ7GzBPWYFo4z0qEJlchYb1JQPz4tTBCJCcPc5dG-m5yjU4VhWSbQY4TaIkvj8R1k9RO_QWIZfuhF-8lXUSqS6/s600/outfit.jpg"));
        list.add(new CatModel("Cartoon", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgRRDYCdmd1bs6RnnjcBGWLwt-jMGFXQjaQ-BfdX1Wg2xhmubsH2LaSdP6aJ6yS8BSX4wGrx7JJK0QI1y6iI4V4ygatYxCw8cMhSmsLRvb2-Z8rVuG41A0Qp5GeY8gurToukWLmrmbfCzm8jrvPU8UKmKpyAxIcXSG2Sk4JY28uuAPym4g3AlCtQcCf/s600/cartoon.jpg"));
        list.add(new CatModel("Other", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiI73TmOIRtqtEgtl1tjh95oo-p4KofFLzPOMBdKurOgilO8kkKSlL97axIR8SdhTaIBo6Fb-j5XYTkvxuE0jfI5fHyp-3QnfVufNFBNRWEyerBez2pWpsFqyp2u0Kh1ZLXXOz__M-Xn-Bl8OjbnA0TScOIPRCJiqSq5Tc50-GHUXPWzDNNVqMZ6gpv/s600/others.jpg"));

        categoryAdapter = new CatAdapter(context, list);
        binding.RVCategories.setLayoutManager(new GridLayoutManager(context, 2));
        binding.PBCategories.setVisibility(View.INVISIBLE);
        binding.RVCategories.setAdapter(categoryAdapter);

        initCat2();

        new Thread(() -> {
            readUrlsFile(false);
            readUrlsFile(true);
            getPopularData();
            getTrendingData();
        }).start();

    }

    private void initCat2() {
        ArrayList<CatModel> list = new ArrayList<>();

        list.add(new CatModel("Other Games Wallpapers", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgVd7rmY6Qfmxg56IxxtNhufi64WDlg2Q4rdxSdjzj6wf0CwrCB_KhYCwcGprN6Ucn54L92zI-1QcaekinH1iaKLdU4AYI42mJ2Lxk4x6BTVyzY3dMBeQJQ-pPKz94OFnAQFAyMLaz5StOZBI-S9DPuALM6vFRJAXAHtuXicWDoJ15hPAqog_Ny3x9-/s600/more.jpg"));
        list.add(new CatModel("Bonus Wallpapers", "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgfR47a_UriaaOKgYgzxNSUZXCqPKZpSGl0q3iIKHTDJFMbx5bfNdTH7FRF1pB3EHgr3JTsTVIwjpBoCATdNv6UM8zRx5i0aHNE6x6z0airQIIY0DAuQXEOPbFclp8yaEREV9Qy5jDEFTLAvVQ0hBNQylnyiiQcrD1abpFYfiqYMUgr0OUJCFGywenW/s600/bonus.jpg"));

        categoryAdapter2 = new CatAdapter2(context, list);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.RVMore.setLayoutManager(manager);
        binding.PBMore.setVisibility(View.INVISIBLE);
        binding.RVMore.setAdapter(categoryAdapter2);

        new Thread(() -> {
            readUrlsFileMore(false);
            readUrlsFileMore(true);
        }).start();
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        this.context = context;
//        activity = (Activity) context;
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.rate_us_menu) {
            PrefManager pref = PrefManager.getInstance(context);
            pref.putBoolean("isAlreadyRated", true);

            CustomReviewManager customReviewManager = new CustomReviewManager(context);
            customReviewManager.playStore(context);
        } else if (item.getItemId() == R.id.share_app_menu) {
            shareApp(context);
        }

        return super.onOptionsItemSelected(item);
    }

    private void readUrlsFile(boolean isKeys) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        Log.d(TAG, "readUrlsFile: yes");

        //Reading the txt file
        InputStream ins;
        if (isKeys)
            ins = context.getResources().openRawResource(R.raw.wallpapers_hash_320);
        else
            ins = context.getResources().openRawResource(R.raw.wallpapers_hash_1000);

        StringBuilder rawData = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));

        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                rawData.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Getting HashMap
        Pattern pattern = Pattern.compile("start:(.*?)end:");
        Matcher matcher = pattern.matcher(rawData);

        int count = 0;
        while (matcher.find()) {
            String urlsList = matcher.group(1);
            count++;

            if (urlsList == null) {
                Log.i(TAG, "readUrlsFile: null list");
                throw new RuntimeException("Something went wrong");
            }

            String[] urls = urlsList.split(",");

            ArrayList<String> aList = new ArrayList<>(Arrays.asList(urls));
            data.add(aList);

        }

        Log.i(TAG, "readUrlsFile: count: " + count);

        Log.d(TAG, "readUrlsFile: time taken: " + (System.currentTimeMillis() - startTime) + "ms");

        if (isKeys) {
            categoryAdapter.settingKeyList(data);

            for (ArrayList<String> aList : data) {
                smallUrls.addAll(aList);
            }
//            uploadUrls(smallUrls);

        } else {
            categoryAdapter.settingValueList(data);

            for (ArrayList<String> aList : data) {
                bigUrls.addAll(aList);
            }
        }

    }

    private void readUrlsFileMore(boolean isKeys) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        Log.d(TAG, "readUrlsFile: yes");

        //Reading the txt file
        InputStream ins;
        if (isKeys)
            ins = context.getResources().openRawResource(R.raw.wallpapers_more_hash_320);
        else
            ins = context.getResources().openRawResource(R.raw.wallpapers_more_hash_1000);

        StringBuilder rawData = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins));

        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                rawData.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Getting HashMap
        Pattern pattern = Pattern.compile("start:(.*?)end:");
        Matcher matcher = pattern.matcher(rawData);

        int count = 0;
        while (matcher.find()) {
            String urlsList = matcher.group(1);
            count++;

            if (urlsList == null) {
                Log.i(TAG, "readUrlsFile: null list");
                throw new RuntimeException("Something went wrong");
            }

            String[] urls = urlsList.split(",");

            ArrayList<String> aList = new ArrayList<>(Arrays.asList(urls));
            data.add(aList);

        }

        Log.i(TAG, "readUrlsFileMore: count: " + count);

        Log.d(TAG, "readUrlsFileMore: time taken: " + (System.currentTimeMillis() - startTime) + "ms");

        if (isKeys) {
            categoryAdapter2.settingKeyList(data);

            for (ArrayList<String> aList : data) {
                smallUrlsMore.addAll(aList);
            }
//            uploadUrls(sUrls);

        } else {
            categoryAdapter2.settingValueList(data);

            for (ArrayList<String> aList : data) {
                bigUrlsMore.addAll(aList);
            }
        }

    }

    private void uploadUrls(ArrayList<String> allUrls) {
        final int[] count = {0};
        ArrayList<String> convertedUrls = convertToFirebaseStyle(allUrls);
        for (String anUrl : convertedUrls) {
            refUrl.child(anUrl).setValue("").addOnCompleteListener(task -> {
                count[0]++;
                Log.i(TAG, "onComplete: Uploaded url: " + count[0] + "/" + allUrls.size());
            });
        }

    }

    static ArrayList<String> convertToFirebaseStyle(ArrayList<String> urls) {
        ArrayList<String> newList = new ArrayList<>();
        for (String anUrl : urls) {
            while (anUrl.contains("/")) {
                anUrl = anUrl.replace("/", ":slash:");
            }
            while (anUrl.contains(".")) {
                anUrl = anUrl.replace(".", ":dot:");
            }
            newList.add(anUrl);
        }
        return newList;
    }

    private ArrayList<String> convertToNormalStyle(ArrayList<String> urls) {
        ArrayList<String> newList = new ArrayList<>();
        for (String anUrl : urls) {
            while (anUrl.contains(":slash:")) {
                anUrl = anUrl.replace(":slash:", "/");
            }
            while (anUrl.contains(":dot:")) {
                anUrl = anUrl.replace(":dot:", ".");
            }
            newList.add(anUrl);
        }
        return newList;
    }

    public ArrayList<String> getArrayList(String key) {
        Gson gson = new Gson();

        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> resultList = gson.fromJson(json, type);
        return (resultList != null) ? resultList : new ArrayList<>();
    }

    public void saveArrayList(ArrayList<String> list, String key) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefs.edit().putString(key, json).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Resume", "onResume: Fragment");
    }

    private void updateDatabaseSession() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.i(TAG, "onSuccess: updateSession: " + databaseUpdateSession);
        try {
            db.collection("Utils").document("l3tLT8Ql2PdSDJBiFPpo").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    @SuppressWarnings("ConstantConditions")
                    long update_session = documentSnapshot.getLong("update_session");
                    databaseUpdateSession = (int) update_session;
                    prefs.edit().putInt("dbUpdateSession_xyz", databaseUpdateSession).apply();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getPopularData() {
//        if (source == Source.SERVER) {
//            Log.i(TAG, "getPopularData: form server!");
//        } else {
//            Log.i(TAG, "getPopularData: form caches!");
//        }
//
//        //Popular Images
//        db.collection("All_Urls_101")
//                .orderBy("popularity", Query.Direction.DESCENDING)
//                .limit(50)
//                .get(source).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
////                List<Wallpaper> data = task.getResult().toObjects(Wallpaper.class);
////                ArrayList<Wallpaper> listPopular = new ArrayList<>(data);
//
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Iterator<QueryDocumentSnapshot> iterator = task.getResult().iterator();
//                        ArrayList<Wallpaper> list = new ArrayList<>();
//                        int count = 0;
//                        while (iterator.hasNext()) {
//                            count++;
//                            QueryDocumentSnapshot snapshot = iterator.next();
//                            Wallpaper wallpaper = new Wallpaper(
//                                    snapshot.getString("sUrl")
//                                    , snapshot.getString("mUrl")
//                                    , snapshot.getString("id")
//                                    , snapshot.getLong("popularity"));
//                            list.add(wallpaper);
//                        }
//
//                        final ArrayList<Wallpaper> finalList = Utility.randomizeArraylist(list);
//
//                        int finalCount = count;
//                        main.execute(() -> {
//                            if (finalCount == 0) {
//                                Log.i(TAG, "run: unable to get popular data! Retying");
//                                getPopularData(Source.SERVER);
//                                return;
//                            } else {
//                                Log.i(TAG, "run: got data popular: " + finalCount);
//                            }
//
//                            PopularAdapter adapter = new PopularAdapter(context, finalList);
//                            binding.RVPopular.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                            binding.PBPopular.setVisibility(View.INVISIBLE);
//                            binding.RVPopular.setAdapter(adapter);
//                        });
//
//                    }
//                }.start();
//
//            }
//
//        });
        long sentTime = System.currentTimeMillis();

        //Check for offline data
        ArrayList<String> savedList = getArrayList("top198Urls_" + ver);
        if (savedList.size() < 1 || currentDatabaseSession % databaseUpdateSession == 0) {
            Log.i(TAG, "getPopularData: from server");
            ArrayList<String> tempTop198Urls = new ArrayList<>();
            int limit = 50;
            final int[] count = {0};

            refUrl.limitToLast(limit).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    count[0]++;

                    tempTop198Urls.add(snapshot.getKey());

                    //listing priorities
//                   increasePopularity(snapshot.getKey());


                    if (count[0] == limit) {
                        Log.i(TAG, "onDataChange: time taken in getting urls: " +
                                "" + (System.currentTimeMillis() - sentTime));

                        new Thread() {
                            @Override
                            public void run() {
                                ArrayList<String> topUrls = convertToNormalStyle(tempTop198Urls);

                                setPopularAdapter(topUrls);

                                saveArrayList(topUrls, "top198Urls_" + ver);

                            }
                        }.start();


                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Log.i(TAG, "getPopularData: form preferences");
            new Thread() {
                @Override
                public void run() {
                    setPopularAdapter(savedList);
                }
            }.start();
        }


    }

    private void getTrendingData() {
//        if (source == Source.SERVER) {
//            Log.i(TAG, "getPopularData: form server!");
//        } else {
//            Log.i(TAG, "getPopularData: form caches!");
//        }
//
//        //Popular Images
//        db.collection("All_Urls_101")
//                .orderBy("popularity", Query.Direction.DESCENDING)
//                .limit(50)
//                .get(source).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
////                List<Wallpaper> data = task.getResult().toObjects(Wallpaper.class);
////                ArrayList<Wallpaper> listPopular = new ArrayList<>(data);
//
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Iterator<QueryDocumentSnapshot> iterator = task.getResult().iterator();
//                        ArrayList<Wallpaper> list = new ArrayList<>();
//                        int count = 0;
//                        while (iterator.hasNext()) {
//                            count++;
//                            QueryDocumentSnapshot snapshot = iterator.next();
//                            Wallpaper wallpaper = new Wallpaper(
//                                    snapshot.getString("sUrl")
//                                    , snapshot.getString("mUrl")
//                                    , snapshot.getString("id")
//                                    , snapshot.getLong("popularity"));
//                            list.add(wallpaper);
//                        }
//
//                        final ArrayList<Wallpaper> finalList = Utility.randomizeArraylist(list);
//
//                        int finalCount = count;
//                        main.execute(() -> {
//                            if (finalCount == 0) {
//                                Log.i(TAG, "run: unable to get popular data! Retying");
//                                getPopularData(Source.SERVER);
//                                return;
//                            } else {
//                                Log.i(TAG, "run: got data popular: " + finalCount);
//                            }
//
//                            PopularAdapter adapter = new PopularAdapter(context, finalList);
//                            binding.RVPopular.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                            binding.PBPopular.setVisibility(View.INVISIBLE);
//                            binding.RVPopular.setAdapter(adapter);
//                        });
//
//                    }
//                }.start();
//
//            }
//
//        });

        long sentTime = System.currentTimeMillis();

        //Check for offline data
        ArrayList<String> savedList = getArrayList("trendTop50_" + ver);
        if (savedList.size() < 1 || currentDatabaseSession % databaseUpdateSession == 0) {
            Log.i(TAG, "getTrend: from server");
            ArrayList<String> tempTop198Urls = new ArrayList<>();
            int limit = 50;
            final int[] count = {0};

            refUrl.limitToFirst(limit).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    count[0]++;

                    tempTop198Urls.add(snapshot.getKey());

                    if (count[0] == limit) {
                        Log.i(TAG, "onDataChange: time taken in getting trend urls: " +
                                "" + (System.currentTimeMillis() - sentTime));

                        new Thread() {
                            @Override
                            public void run() {
                                ArrayList<String> topUrls = convertToNormalStyle(tempTop198Urls);

                                setTrendAdapter(topUrls);

                                saveArrayList(topUrls, "trendTop50_" + ver);

                            }
                        }.start();


                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Log.i(TAG, "getTrend: form preferences");
            new Thread() {
                @Override
                public void run() {
                    setTrendAdapter(savedList);
                }
            }.start();
        }


    }

    private void setPopularAdapter(ArrayList<String> topUrls) {
        ArrayList<WallpaperModel> list = new ArrayList<>();
        for (String url : topUrls) {
            int indexInMain = smallUrls.indexOf(url);
            String mainUrl = bigUrls.get(indexInMain);
            list.add(new WallpaperModel(url, mainUrl));
        }

        final ArrayList<WallpaperModel> finalList = Utils.randomizeArraylist(list);

        executor.execute(() -> {
            PopAdapter adapter = new PopAdapter(context, finalList);
            binding.RVPopular.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            binding.PBPopular.setVisibility(View.INVISIBLE);
            binding.RVPopular.setAdapter(adapter);
        });

    }

    private void setTrendAdapter(ArrayList<String> topUrls) {
        ArrayList<WallpaperModel> list = new ArrayList<>();
        for (String url : topUrls) {
            int indexInMain = smallUrls.indexOf(url);
            String mainUrl = bigUrls.get(indexInMain);
            list.add(new WallpaperModel(url, mainUrl));
        }

        final ArrayList<WallpaperModel> finalList = Utils.randomizeArraylist(list);

        executor.execute(() -> {
            PopAdapter adapter = new PopAdapter(context, finalList);
            binding.RVTrending.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            binding.PBTrending.setVisibility(View.INVISIBLE);
            binding.RVTrending.setAdapter(adapter);
        });

    }

    public static void shareApp(Context context) {
        try {
            String msg = "Take a look at this Awesome Free Fire Wallpaper App ";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Free Fire Wallpapers");
            String shareMessage = "\n" + msg + "\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}