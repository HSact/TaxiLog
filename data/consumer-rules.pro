# Сохраняем модели Firebase для корректной работы рефлексии Firestore
-keep class com.hsact.data.firebase.model.** { *; }

# Room Entities (защита от переименования полей, если они используются в рефлексии или маппинге)
-keep class com.hsact.data.db.entities.** { *; }
