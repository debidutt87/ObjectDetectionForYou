
# Ody (Object Detection for You) 📸

Ody is an Android application designed to perform object detection on images captured by the user. With the integration of the `efficientdet_lite2_.tflite` model, Ody identifies and highlights objects within images, streamlining the image analysis process for users.

## Thought Process 
1. **Navigation Blue Print:**

**Significance:** The navigation blueprint, often referred to as the app's navigation graph or flow, dictates how users move between different parts of your application. A well-defined navigation ensures that the user experience is intuitive and consistent.
**Considerations:** You should think about which screens or fragments users will navigate to frequently, which ones they should be able to return to easily, and how they'll be directed in case of errors. Using tools like Android's Navigation component can assist in visualizing and implementing navigation more effectively.

2. **Storing Images in App's Private Memory:**

**Significance:** The way you store data, especially larger data types like images, has a big impact on the performance, security, and user experience of your app.
**Considerations:** Storing images in the app's private memory ensures that other apps cannot access them, which is good for security. However, it also means that the images are deleted if the user uninstalls the app. Depending on the size and number of images, using private storage can also fill up the device's internal storage quickly, which can be a concern for users with limited storage.

3.**Choosing MVVM (Model-View-ViewModel):**

**Significance:** The architecture pattern you choose for your app will influence its maintainability, testability, and the ease with which you can scale or modify features in the future.
**Considerations:** MVVM promotes a separation of concerns, making it easier to understand the code and write tests. It pairs well with Android's LiveData and Data Binding, which can result in more concise and reactive UI code. However, as with any pattern, it's crucial to avoid common pitfalls, such as bloated ViewModels or ignoring the importance of Dependency Injection.

4.**Next steps to make this repo better:**
   a. Migrate from programmatic way of navigation to navigation graph.
   b. Write unit tests and UI tests.
   c. Make a pipeline to generate a build.

## Features 🌟

1. **Welcome Page**: Introduces users to the app and guides them through the steps to capture an image and initiate object detection.
2. **Object Detection**: Uses the `efficientdet_lite2_.tflite` model to analyze images and identify objects.
3. **Interactive UI**: If an object's confidence score exceeds 30%, Ody will draw a rectangle around the identified object.
4. **Object List**: All detected objects are presented in a recycler view, offering users an organized and comprehensive list of detected items.
5. **Local Database**: Users have the option to save their image analysis results in a local database and can retrieve them at any time.

Certainly!

---

## Getting Started 🚀

### Prerequisites

- **Android Studio**: To run and test the app, ensure you have [Android Studio](https://developer.android.com/studio) installed on your machine.
  
- **Android Device or Emulator**: For best results and to use camera-related functionalities, testing on an actual Android device is recommended. However, you can also use Android Studio's built-in emulator.
  
- **Java SDK**: Ensure you have Java SDK installed and properly configured. This app specifically uses Java 8, so ensure your JDK setup corresponds to this.

### Steps to Run

1. **Clone the Repository**: 
   
   ```
   git clone https://github.com/YourUsername/Ody.git
   ```

2. **Open in Android Studio**: Launch Android Studio and select `Open an existing Android Studio project`. Navigate to the cloned repository and select it.

3. **Sync and Build**: Once the project is opened, sync the project with gradle files. This will download the necessary dependencies. After syncing, build the project.

4. **Run the App**: Click on the `Run` button (a green play icon) or press `Shift + F10` to launch the app on your connected Android device or emulator.

5. **Start Detecting Objects!** Upon launching, you'll be presented with the Welcome Page. Follow the steps to capture an image and let Ody work its magic.


## Setup and Configuration 🛠️

### Plugins

```groovy
plugins {
    id 'com.android.application'
}
```

### Android Configuration

```groovy
android {
    compileSdk 33
    defaultConfig {
        applicationId "com.ody.di"
        minSdk 29
        targetSdk 33
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding true
    }
}
```

### Dependencies 📦

Your app uses a range of dependencies from UI to database storage. Here are some of the main libraries integrated:

- AppCompat and Material Design components for modern UI elements.
- ConstraintLayout for flexible and responsive layouts.
- Navigation components for effective fragment and UI navigation.
- Room for local data persistence and database operations.
- Glide for efficient image loading and caching.
- Gson for JSON operations.

For a complete list of dependencies and versions, check the `build.gradle` file.

---

## Development and Contribution

Feel free to fork this project and submit your PRs for enhancements and bug fixes. Make sure to follow the existing coding patterns and standards.

**MVVM Architecture**
MVVM (Model-View-ViewModel) is a widely-adopted architectural pattern for building maintainable, testable, and scalable Android applications. In "Ody", we've chosen MVVM as our guiding architecture for its several advantages:

Components:
- Model: Represents the data and business logic of the app. It's responsible for fetching, storing, and providing data. The Model is unaware of the View or ViewModel.

- View: Represents the UI of the application. It observes the ViewModel for any data changes.

- ViewModel: Acts as a link between the Model and the View. It's responsible for transforming data from the Model into a format that can be displayed by the View. Moreover, it handles user interactions and updates the Model accordingly.

**Advantages:**
- Decoupling of Logic and UI: MVVM promotes separation of concerns. Business logic is moved away from the UI layer, making the code more modular and easier to maintain.

- Testability: With the separation of concerns and decoupling, it's easier to write unit tests for the ViewModel and Model without worrying about UI complexities.

- Data Binding: MVVM works seamlessly with data binding, allowing automatic UI updates when the data changes, reducing boilerplate code.

- Lifecycle-aware: ViewModel is designed to be lifecycle-aware, meaning it can handle configuration changes like screen rotations more efficiently than traditional methods.

In "Ody", we've incorporated these principles to ensure that our app remains robust, maintainable, and efficient. If you're interested in diving deeper, the codebase provides practical examples of how MVVM is implemented in a real-world application.

## Contributions 🤝

Contributions to this project are welcome! Here's how you can help:

1. **Fork and Clone**:
   Begin by forking this repository to your own GitHub account and then clone it to your local device.
   
2. **Create a New Branch**:
   Create a new branch for your feature or fix:
   
   ```bash
   git checkout -b my-new-feature
   ```

3. **Make Your Changes**: 
   Implement and test your feature. Ensure it's working as expected.

4. **Commit and Push**:
   Commit your changes and push to your forked repository:
   
   ```bash
   git commit -m "Added a new feature"
   git push origin my-new-feature
   ```

5. **Open a Pull Request (PR)**:
   Go to your forked repo on GitHub and click on the 'New Pull Request' button. Fill in the necessary details and submit.

6. **Review**:
   Wait for your PR to be reviewed. Address any changes or fixes as requested.


---

## Acknowledgments 🙏

- **[EfficientDet](https://github.com/google/automl/tree/master/efficientdet)**: Our object detection is powered by the EfficientDet model, which provides a balance of speed and accuracy.

- **[Glide](https://github.com/bumptech/glide)**: For efficiently loading and caching images.

- **Android Community**: For the plethora of resources, tutorials, and code snippets that helped in building this app.

- All the beta testers and users who provided invaluable feedback to make the app better.


## License

This project is licensed under the MIT License.

## Author

Developed by Debidutt Prasad.

