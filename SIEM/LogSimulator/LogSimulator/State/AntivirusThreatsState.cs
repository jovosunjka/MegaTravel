using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class AntivirusThreatsState : IState
    {
        private static int _threatId = 1;
        public string Description => Helper.Constants.StateDescription.AntivirusThreats;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var count = int.Parse(appSettings.AntivirusThreatsNum);
            for (int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"Anstivirus found threat with id '{_threatId++}'", LogLevelType.WARN);
                logService.WriteLogToFile(appSettings.LogsFilePath, log);
            }
        }
    }
}