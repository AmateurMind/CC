# Experiment 1 - Flask App Engine Starter

This project is a simple Flask application intended to run on Google App Engine.

## Run locally

```powershell
cd F:\CC\exp1
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python main.py
```

Then open:

```text
http://127.0.0.1:8080
```

## Deploy to Google App Engine

```powershell
cd F:\CC\exp1
gcloud app deploy
```
