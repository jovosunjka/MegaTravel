using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;
using System;

namespace LogSimulator.State
{
    /// <summary>
    /// Uspešna prijava na sistem praćena sa izmenom korisničkih podataka ukoliko je sa iste IP adrese u poslednjih 90 sekundi bilo registrovano 5 ili više neuspešnih pokušaja prijavljivanja na različite naloge
    /// </summary>
    public class ChangeUserInfoState : IState
    {
        public string Description => Helper.Constants.StateDescription.ChangeUserInfo;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var ipAddress = appSettings.MaliciousIpAddress;
            for (int i = 0; i < 5; i++)
            {
                var usernameLog = logService.GetLog($"Login attempt with username '{appSettings.Username}' from ip address '{ipAddress}'", LogCategory.LOGIN, LogLevelType.WARN);
                logService.WriteLogToFile(appSettings.LoginLogsFolderPath, usernameLog);
            }
            for (int i = 0; i < 5; i++)
            {
                var username2Log = logService.GetLog($"Login attempt with username '{appSettings.Username2}' from ip address '{ipAddress}'", LogCategory.LOGIN, LogLevelType.WARN);
                logService.WriteLogToFile(appSettings.LoginLogsFolderPath, username2Log);
            }
            var loginLog = logService.GetLog($"Login success with username '{appSettings.Username2}' from ip address '{ipAddress}'", LogCategory.LOGIN);
            var changeLog = logService.GetLog($"User with username '{appSettings.Username2}' changes password settings", LogCategory.APP);
            logService.WriteLogToFile(appSettings.LoginLogsFolderPath, loginLog);
            logService.WriteLogToFile(appSettings.LoginLogsFolderPath, changeLog);
        }
    }
}