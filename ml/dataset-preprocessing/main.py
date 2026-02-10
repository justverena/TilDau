import csv
import os
from tqdm import tqdm
from preprocessing import process_file
from audio_io import load_raw_metadata

base_dir = os.path.dirname(os.path.abspath(__file__))
project_root = os.path.abspath(os.path.join(base_dir, "../../"))

input_audio = os.path.join(project_root, "data/dataset-raw/audio")
raw_metadata_path = os.path.join(project_root, "data/dataset-raw/metadata.csv")

output_audio = os.path.join(project_root, "data/dataset-processed/audio")
processed_metadat_path =  os.path.join(project_root, "data/dataset-processed/metadata.csv")

def preprocess_dataset():
    raw_metadata = load_raw_metadata(raw_metadata_path)
    processed_rows = []

    for row in tqdm(raw_metadata, desc=f"Processing audio"):
        fname = row ["path"]
        in_path = os.path.join(input_audio, fname)
        out_path = os.path.join(output_audio, fname)

        if not os.path.exists(in_path):
            print(f"Missing audio file: {fname}")
            continue

        try:
            features = process_file(in_path, out_path)
            merged_row = {
                **row,
                **features
            }

            processed_rows.append(merged_row)

        except Exception as e:
            print (f"Error processing {fname}: {e}")

    os.makedirs(os.path.dirname(processed_metadat_path), exist_ok=True)

    fieldnames = list(processed_rows[0].keys())

    with open(processed_metadat_path, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(processed_rows)

    print(f"\nSaved preprocessed metadata to {processed_metadat_path}")

if __name__ == "__main__":
    preprocess_dataset()