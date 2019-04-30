package com.bsep_sbz.SIEMCenter.model.sbz.enums;

public enum LogLevel {
    // razdvojiti developerske logove od security logova u razlicite fajlove
    // bilo bi fino da agent sam skonta na kom OS-u se nalazi
    // treba definisati listu fajlova i foldera koje treba agenti (Windows, Linux) da citaju
    // za svaki izvor (fajl, log) treba definisati rezim rada (batch(periodicno), real-time),
    // vrstu logova koja treba bude slata u odredjenom rezimu

    //owasp dependency check proverava ranjivosti biblioteka prilikom builda

    INFO, WARN, ERROR, DEBUG
}
