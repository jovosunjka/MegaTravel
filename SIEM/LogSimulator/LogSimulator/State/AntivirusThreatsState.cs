using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    /// <summary>
    /// U periodu od 10 dana registrovano 7 ili više pretnji od strane antivirusa za isti računar
    /// Pojava loga u kome antivirsu registruje pretnju, a da u roku od 1h se ne generise log o uspešnom elimisanju pretnje
    /// </summary>
    public class AntivirusThreatsState : IState
    {
        public string Description => Helper.Constants.StateDescription.AntivirusThreats;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var count = int.Parse(appSettings.AntivirusThreatsNum);
            for (int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"Anstivirus found threat with id '{ logService.GetNextAntivirusThreatId() }'", LogLevelType.WARN);
                logService.WriteLogToFile(appSettings.AntivirusLogsFolderPath, log);
            }
        }
    }
}