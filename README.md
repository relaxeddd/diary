# diary (iOS Kotlin/Native)

Currently no Android implementation. 
Used MVVM layers. Platform contains only View layer.

## Geting started
Cocoapods is making build complex for now
1. `Sync Project with Gradle Files`
2. `./gradlew podspec`
3. `cd iosApp` and `pod install`
4. Run on iOS device

## Libraries Used:

* Ktor
* SQLDelight
* Firebase Auth (Integrated with Kotlin)
* Google Cloud Functions

## TODO
1. Inject dependencies of other view models via constructor like in ViewModelAuth. Make View Models internal and expose them by ViewModelCreator.
2. Refactor iOS storyboard with Storyboard References

## Screenshots

<details>
  <summary>Screenshots</summary>
  
![IMG_0267](https://user-images.githubusercontent.com/26667352/112753590-bf4ef080-8fe0-11eb-9928-7bc65aa3b894.png)
![IMG_0268](https://user-images.githubusercontent.com/26667352/112753593-c249e100-8fe0-11eb-9e61-eec8f82e6c5c.png)
![IMG_0269](https://user-images.githubusercontent.com/26667352/112753594-c2e27780-8fe0-11eb-96bd-0495a0ef25a8.png)
![IMG_0271](https://user-images.githubusercontent.com/26667352/112753601-caa21c00-8fe0-11eb-8ae3-429eb343f664.png)
  
</details>
