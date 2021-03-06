﻿using LogSimulator.Model;
using LogSimulator.Model.Enum;

namespace LogSimulator.Service.Interface
{
    public interface ILogService
    {
        void WriteLogToFile(string path, Log log);

        Log GetLog(string message, LogCategory logCategory, LogLevelType logLevelType = LogLevelType.INFO);

        long GetNextLogId();

        long GetNextAntivirusThreatId();
    }
}
