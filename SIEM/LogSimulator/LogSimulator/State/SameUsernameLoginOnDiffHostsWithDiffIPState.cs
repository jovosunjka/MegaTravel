using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class SameUsernameLoginOnDiffHostsWithDiffIpState : IState
    {
        public string Description => Helper.Constants.StateDescription.SameUsernameLoginOnDiffHostsWithDiffIp;

        public void Simulate(IAppSettings appSettings)
        {
            string logContent1 = $"User with username'{appSettings.Username}' successfully logged in on host '{appSettings.HostAddress1}' from ip address {appSettings.IpAddress1}\n";
            string logContent2 = $"User with username'{appSettings.Username}' successfully logged in on host '{appSettings.HostAddress2}' from ip address {appSettings.IpAddress2}\n";
            System.IO.File.AppendAllText(appSettings.LogsFilePath, logContent1);
            System.IO.File.AppendAllText(appSettings.LogsFilePath, logContent2);
        }
    }
}
