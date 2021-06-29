# Menu Planner Android App
Android app developed for my bachelor thesis in IT engineering to generate custom weekly food menus.

This app generates a weekly menu, including breakfast, meal and dinner for each day. Every menu is generated based on different parameters provided by the user related to his physical condition and preferred foods.

The app follows the MVVM architecture, with a repository that has local data in a Room database, remote data in Firebase and uses Spoonacular API to obtain the recipes through Retrofit.

You can check how the app works and its interface reading the bachelor thesis written in spanish here: https://repositorio.uam.es/handle/10486/693402

**Requisites:**
In order to use the app, you need to get a new Spoonacular API key, add it as a string resource and create a new Firebase project that needs to be linked later to the Android Studio project in order to use the remote database and the authentication system.
