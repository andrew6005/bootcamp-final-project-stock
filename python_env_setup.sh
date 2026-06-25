#!/usr/bin/env bash
set -e

if type deactivate >/dev/null 2>&1; then
  echo "Deactivating environment..."
  deactivate
fi

if [ -d "bootcamp-env" ]; then
  echo "Removing old virtual environment..."
  rm -rf bootcamp-env
fi

echo "Creating new virtual environment..."
python3 -m venv bootcamp-env

echo "Activating virtual environment..."
source bootcamp-env/bin/activate

pip install --upgrade pip
pip install -r requirements.txt
python -m ipykernel install --user --name=bootcamp-env --display-name "Python (bootcamp-env)"

pip --version
python --version
jupyter --version

echo "Environment setup complete."
