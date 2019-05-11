using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    /// <summary>
    /// Pojava loga čiji tip je ERROR
    /// </summary>
    public class LogErrorState : IState
    {
        public string Description => Helper.Constants.StateDescription.LogError;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var errorLog = logService.GetLog("Exception in thread main java.lang.NullPointerException", LogLevelType.ERROR);
            var traceLog1 = logService.GetLog("at com.example.myproject.Flight.getTitle(Flight.java:16)", LogLevelType.TRACE);
            var traceLog2 = logService.GetLog("at com.example.myproject.Ticket.getBookTitles(Ticket.java:25)", LogLevelType.TRACE);
            // trace logs are related to same (error) event
            var eventId = errorLog.EventId;
            traceLog1.EventId = eventId;
            traceLog2.EventId = eventId;

            logService.WriteLogToFile(appSettings.LogsFilePath, errorLog);
            logService.WriteLogToFile(appSettings.LogsFilePath, traceLog1);
            logService.WriteLogToFile(appSettings.LogsFilePath, traceLog2);
        }
    }
}
