using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class LoginWithCommonIpState : IState
    {
        public string Description => Helper.Constants.StateDescription.LoginWithCommonIp;

        public void Simulate(IAppSettings appSettings, ILogService logService)
        {
            var log = logService.GetLog($"Login success with username '{appSettings.Username}' from ip address '{appSettings.IpAddress2}'");
            logService.WriteLogToFile(appSettings.LogsFilePath, log);
        }
    }
}
