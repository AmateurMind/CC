"""Post Office Finder application for Google App Engine."""

from flask import Flask, render_template, request
import requests

app = Flask(__name__)
POSTAL_PIN_URL = "https://api.postalpincode.in/pincode/{}"


@app.route("/", methods=["GET", "POST"])
def index():
    if request.method == "GET":
        return render_template("index.html")

    pincode = request.form.get("zipCode", "").strip()
    if not (pincode.isdigit() and len(pincode) == 6):
        return render_template(
            "index.html", error="Enter a valid six-digit PIN code."
        )

    try:
        response = requests.get(
            POSTAL_PIN_URL.format(pincode),
            headers={
                "Accept": "application/json",
                "User-Agent": "Post-Office-Finder/1.0",
            },
            timeout=10,
        )
        response.raise_for_status()
        data = response.json()
        offices = data[0].get("PostOffice") if data else None
        if data[0].get("Status") != "Success" or not offices:
            return render_template(
                "index.html", error="No post office was found for this PIN code."
            )
    except (requests.RequestException, ValueError, KeyError, IndexError):
        return render_template(
            "error.html",
            message="The postal service is unavailable right now. Please try again.",
        ), 503

    office = offices[0]
    return render_template(
        "results.html",
        state=office.get("State", "Not available"),
        name=office.get("Name", "Not available"),
        block=office.get("Block", "Not available"),
        district=office.get("District", "Not available"),
        pincode=pincode,
    )


if __name__ == "__main__":
    app.run(host="127.0.0.1", port=8080, debug=True)
