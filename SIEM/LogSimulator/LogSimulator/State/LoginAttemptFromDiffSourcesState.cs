using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class LoginAttemptFromDiffSourcesState : IState
    {
        public string Description => Helper.Constants.StateDescription.LoginAttemptFromDiffSources;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var count = int.Parse(appSettings.UnsuccessfulLoginAttemptNum);
            for (int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"Login attempt with username '{appSettings.Username}' from ip address '127.{i}2.5{i}.11'", LogCategory.LOGIN, LogLevelType.WARN);
                logService.WriteLogToFile(appSettings.LoginLogsFolderPath, log);
            }
        }
    }
}
