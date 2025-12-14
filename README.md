# Timer

**Statistical Timing Side-Channel Analysis for Burp Suite**

TIMER is a Burp Suite extension that performs statistical analysis of HTTP response timing in order to identify timing-based side channels.  
It helps penetration testers determine whether two classes of requests (e.g. valid vs invalid identifiers) are distinguishable based on response latency.

The extension computes common statistical measures used in timing attacks, including AUC (Area Under Curve), Mann–Whitney U, Cohen’s *d*, and signal-to-noise ratio, and presents them in a pentester-friendly UI.

## Features

- Capture and separate timing samples into two request pools
- Statistical comparison of response latency distributions
- AUC-based distinguishability scoring with human-readable interpretation
- Additional descriptive statistics (mean, median, min/max, etc.)
- Designed for timing-based enumeration and inference attacks
- Fully local — no data leaves Burp Suite

## Installation

### Option 1 — Download prebuilt JAR (recommended)

1. Go to the **Releases** page of this repository
2. Download the latest `timer.jar`
3. In Burp Suite:
    - Open **Extensions → Installed**
    - Click **Add**
    - Select **Extension type: Java**
    - Load the downloaded JAR


### Option 2 — Build from source

Requirements:
- Java 17+
- Gradle

```bash
git clone https://github.com/anvilventures/timer.git
cd timer
./gradlew bigJar
```

The fat JAR will be generated under:

```bash
build/libs/
```

Load it into Burp Suite as a Java extension.

## Usage
Work in progress ⚠️