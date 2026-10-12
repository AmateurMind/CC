# Experiment 2 - Post Office Finder

This is the web application from the Experiment 2 write-up, updated for the supported Python 3 App Engine runtime.

## Run locally

```powershell
cd F:\CC\2nd
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python main.py
```

Open http://127.0.0.1:8080 and enter a six-digit Indian PIN code.

## Deploy to Google App Engine

After installing and authenticating the Google Cloud CLI, run:

```powershell
gcloud app deploy
```
