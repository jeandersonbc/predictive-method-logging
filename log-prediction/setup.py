from setuptools import setup

setup(
    name="logpred_method",
    version=0.1,
    description="Prediction of log placement at method level",
    url="https://github.com/jeandersonbc/predictive-method-logging",
    author="Jeanderson Candido",
    author_email="j.barroscandido@tudelft.nl",
    packages=["logpred_method"],
    zip_safe=False,
    entry_points={
        "console_scripts": ["logpred-method=logpred_method.command_line:main"]
    },
    python_requires=">=3.6",
    install_requires=[
        "imbalanced-learn==0.7.0",
        "scikit-learn==0.23.1",
        "pandas==1.0.4",
        "numpy==1.18.5",
    ],
)
