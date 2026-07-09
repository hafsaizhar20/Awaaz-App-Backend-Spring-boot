import os
import psycopg2
from deep_translator import GoogleTranslator

# Parse .env file
db_config = {}
env_path = ".env"
if os.path.exists(env_path):
    with open(env_path, "r") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            parts = line.split("=", 1)
            if len(parts) == 2:
                db_config[parts[0].strip()] = parts[1].strip()

# Extract database parameters
db_url = db_config.get("DB_URL")
username = db_config.get("DB_USERNAME")
password = db_config.get("DB_PASSWORD")

# Default connection settings
host = "aws-1-ap-northeast-2.pooler.supabase.com"
port = 6543
database = "postgres"

if db_url:
    clean_url = db_url.replace("jdbc:postgresql://", "")
    if "?" in clean_url:
        clean_url = clean_url.split("?")[0]
    parts = clean_url.split("/")
    database = parts[1]
    host_port = parts[0].split(":")
    host = host_port[0]
    if len(host_port) > 1:
        port = int(host_port[1])

print(f"Connecting to database '{database}' on {host}:{port} as {username}...")

try:
    conn = psycopg2.connect(
        host=host,
        port=port,
        database=database,
        user=username,
        password=password
    )
    cursor = conn.cursor()
    translator = GoogleTranslator(source='en', target='ur')

    # 1. Update categories
    print("\n--- Translating Categories ---")
    cursor.execute("SELECT id, name, name_ur FROM aac_categories")
    categories = cursor.fetchall()
    
    updated_categories = 0
    for cid, name, name_ur in categories:
        if not name_ur:
            try:
                translated_name = translator.translate(name)
                print(f"Category '{name}' -> '{translated_name}'")
                cursor.execute(
                    "UPDATE aac_categories SET name_ur = %s WHERE id = %s",
                    (translated_name, cid)
                )
                updated_categories += 1
            except Exception as e:
                print(f"Error translating category '{name}': {e}")
                
    conn.commit()
    print(f"Successfully updated {updated_categories} categories.")

    # 2. Update icons (subcategories)
    print("\n--- Translating Icons (Subcategories) ---")
    cursor.execute("SELECT id, label, label_ur, speech_text, speech_text_ur FROM aac_icons")
    icons = cursor.fetchall()
    
    updated_icons = 0
    for icon_id, label, label_ur, speech_text, speech_text_ur in icons:
        needs_update = False
        new_label_ur = label_ur
        new_speech_text_ur = speech_text_ur
        
        if not label_ur:
            try:
                new_label_ur = translator.translate(label)
                print(f"Icon Label '{label}' -> '{new_label_ur}'")
                needs_update = True
            except Exception as e:
                print(f"Error translating icon label '{label}': {e}")
                
        if not speech_text_ur:
            if speech_text == label and new_label_ur:
                new_speech_text_ur = new_label_ur
                needs_update = True
            elif speech_text:
                try:
                    new_speech_text_ur = translator.translate(speech_text)
                    print(f"Icon Speech Text '{speech_text}' -> '{new_speech_text_ur}'")
                    needs_update = True
                except Exception as e:
                    print(f"Error translating speech text '{speech_text}': {e}")
                    
        if needs_update:
            cursor.execute(
                "UPDATE aac_icons SET label_ur = %s, speech_text_ur = %s WHERE id = %s",
                (new_label_ur, new_speech_text_ur, icon_id)
            )
            updated_icons += 1
            
    conn.commit()
    print(f"Successfully updated {updated_icons} icons.")

    cursor.close()
    conn.close()
    print("\nTranslation script completed successfully!")
    
except Exception as e:
    print("Database Connection Error:", e)
