using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class LoginWithMaliciousIpState : IState
    {
        public string Description => Helper.Constants.StateDescription.LoginWithMaliciousIp;

        public void Simulate(IAppSettings appSettings)
        {
            System.IO.File.AppendAllText(appSettings.LogsFilePath, $"Login attempt from ip address '{appSettings.MaliciousIpAddress}'\n");
        }
    }
}
