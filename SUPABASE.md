# Supabase Setup for HackerNews-KMP

This project uses Supabase for backend services. To run the app, you need to create your own Supabase project and configure the app to use it.

## 1. Create a Supabase Project

1.  Go to [supabase.com](https://supabase.com/) and sign in or create an account.
2.  Create a new project. You can use the free tier.
3.  During project creation, or after, make sure to save your **Project URL** and **anon public API Key**.

## 2. Create the user_configs Table

Run the following SQL in the Supabase SQL Editor to create the table used by the app:

```sql
CREATE TABLE public.user_configs (
  user_id uuid NOT NULL,
  key text NOT NULL,
  value text NOT NULL,
  updated_at timestamp with time zone DEFAULT now(),
  CONSTRAINT user_configs_pkey PRIMARY KEY (user_id, key),
  CONSTRAINT user_configs_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
```

## 3. URL Configuration

1. In Supabase project dashboard, go to Authentication -> URL configuration
2. Set site-url to match the SiteURL here in the app: `pulseapp://auth-callback`

## 4. Find Your Project Credentials

1.  In your Supabase project dashboard, go to **Project Settings**.
2.  Click on the **API** tab.
3.  You will find your **Project URL** and your **Project API Keys** there. You need the `anon` `public` key.

## 5. Configure Your Local Environment

You need to provide the Supabase URL and Key to the application via Gradle properties.

1.  Create or open the `gradle.properties` file in your user's Gradle home directory.
    *   On macOS and Linux, this is typically `~/.gradle/gradle.properties`.
    *   On Windows, this is typically `%USERPROFILE%\.gradle\gradle.properties`.

2.  Add your Supabase credentials to this file, like so:

    ```properties
    PULSE_SUPABASE_URL=YOUR_SUPABASE_URL
    PULSE_SUPABASE_KEY=YOUR_SUPABASE_ANON_KEY
    ```

    Replace `YOUR_SUPABASE_URL` and `YOUR_SUPABASE_ANON_KEY` with the actual values from your Supabase project.

The application is configured to read these values at build time. Make sure to sync your project in Android Studio or IntelliJ after creating the file.
