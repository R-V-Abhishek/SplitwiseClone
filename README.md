# Splitwise Clone - Android App

A native Android application built with Jetpack Compose that mimics the core functionality of Splitwise. It allows users to create groups, add expenses, and automatically calculates who owes whom to settle debts.

This project was built as a learning exercise to understand modern Android development, including UI with Jetpack Compose, state management, navigation, and core business logic implementation in Kotlin.

## ✨ Features

- **Group Management:** Create and view groups for different events or shared living spaces.
- **Expense Tracking:** Add new expenses to a group, specifying who paid and how the cost should be split.
- **Automatic Debt Calculation:** A smart algorithm calculates the simplest set of transactions required to settle all debts within a group.
- **Clean, Modern UI:** Built entirely with Jetpack Compose, following Material Design 3 principles.
- **Single-Screen Experience:** Uses dialogs for quick actions like adding groups and expenses without navigating away from the main screens.
- **Navigation:** Uses Navigation for Compose to move between the main groups list and the detailed view of each group.

## 🛠️ Built With

- **[Kotlin](https://kotlinlang.org/):** The official programming language for Android development.
- **[Jetpack Compose](https://developer.android.com/jetpack/compose):** Android's modern, declarative UI toolkit.
- **[Material 3](https://m3.material.io/):** The latest version of Google's design system.
- **[Navigation for Compose](https://developer.android.com/jetpack/compose/navigation):** For handling navigation between screens.
- **State Management:** Utilizes `remember` and `mutableStateOf` for managing UI state within composables.

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

- Android Studio Iguana | 2023.2.1 or newer
- An Android device or emulator running API level 24 or higher

### Installation

1.  Clone the repo
    ```sh
    git clone https://github.com/R-V-Abhishek/SplitwiseClone.git
    ```
2.  Open the project in Android Studio.
3.  Let Gradle sync the dependencies.
4.  Run the app on your emulator or physical device.

## 🧠 Core Logic: The Split Algorithm

The settlement logic is located in the `SplitLogic.kt` file. It works in two main steps:

1.  **Calculate Net Balances:** It iterates through all expenses to compute a final balance for each member.
    - `Balance = (Total Paid) - (Total Owed)`
    - A positive balance means the member is owed money (a creditor).
    - A negative balance means the member owes money (a debtor).

2.  **Simplify Transactions:** It then takes the lists of creditors and debtors and creates the minimum number of transactions required to bring everyone's balance back to zero.

Built with Love - R V