# Menu Planner Android App
Aplicación Android desarrollada para mi TFG en Ingeniería Informática para generar menús semanales.

Esta app genera un menú semanal, con el desayuno, comida y cena para cada día. Cada menú se genera basándose en diferentes parámetros proporcionados por el usuario en función de su condición física y sus comidas favoritas. De este modo el algoritmo de recomendación se adapta a cada usuario y va aprendiendo de sus gustos según va utilizando la aplicación.

La aplicación sigue el patrón de presentación MVVM, con un repositorio que gestiona una base de datos local con Room, un repositorio remoto en Firebase y utiliza la API de Spoonacular para obtener las recetas a través de Retrofit.

Puedes ver como funciona la aplicación y su interfaz consultando la memoria del TFM en el siguiente enlace: https://repositorio.uam.es/handle/10486/693402

**Requisitos:**
Para poder utilizar la aplicación es necesario conseguir una key de la API de Spoonacular, añadirla al proyecto y crear un nuevo proyecto en Firebase enlazado al proyecto de Android Studio para poder utilizar la base de datos remota y el sistema de autenticación.
