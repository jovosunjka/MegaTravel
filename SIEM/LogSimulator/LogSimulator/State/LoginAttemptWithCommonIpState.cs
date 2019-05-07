using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class LoginAttemptWithCommonIpState : IState
    {
        public string Description => Helper.Constants.StateDescription.LoginAttemptWithCommonIp;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var count = int.Parse(appSettings.UnsuccessfulLoginAttemptNum);
            for (int i = 0; i < count; i++)
            {
                var log = logService.GetLog($"Login attempt with username '{appSettings.Username}' from ip address '{appSettings.IpAddress2}'");
                logService.WriteLogToFile(appSettings.LogsFilePath, log);
            }
        }
    }
}
