using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class UnsuccessfulLoginsOnDiffHostsWithSameIpState : IState
    {
        public string Description => Helper.Constants.StateDescription.UnsuccessfulLoginsOnDiffHostsWithSameIp;

        public void Simulate(IAppSettings appSettings)
        {
            var count = int.Parse(appSettings.UnsuccessfulLoginAttemptNum);
            string ipAddress = appSettings.IpAddress1;
            string[] hosts = new string[] { appSettings.HostAddress1, appSettings.HostAddress2, appSettings.HostAddress3 };
            for (int i = 0; i < count; i++)
            {
                string logContent = $"Unsuccessful login on host '{ hosts[i%3] }' from ip address '{ipAddress}'\n";
                System.IO.File.AppendAllText(appSettings.LogsFilePath, logContent);
            }
        }
    }
}
