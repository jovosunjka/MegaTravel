using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class LogErrorState : IState
    {
        public string Description => Helper.Constants.StateDescription.LogError;

        public void Simulate(IAppSettings appSettings)
        {
            System.IO.File.AppendAllText(appSettings.LogsFilePath, $"ERROR\n");
        }
    }
}
