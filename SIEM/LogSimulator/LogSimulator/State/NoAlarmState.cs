using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class NoAlarmState : IState
    {
        public string Description => Helper.Constants.StateDescription.NoAlarm;

        public void Simulate(IAppSettings appSettings)
        {
            System.IO.File.AppendAllText(appSettings.LogsFilePath, $"Some ordinary log\n");
        }
    }
}
