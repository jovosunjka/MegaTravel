using LogSimulator.Service.Interface;

namespace LogSimulator.State
{
    public class AntivirusThreatsOnSameHostState : IState
    {
        private static int _threatId = 1;
        public string Description => Helper.Constants.StateDescription.AntivirusThreatsOnSameHost;

        public void Simulate(IAppSettings appSettings)
        {
            var count = int.Parse(appSettings.AntivirusThreatsNum);
            for (int i = 0; i < count; i++)
            {
                System.IO.File.AppendAllText(appSettings.LogsFilePath, $"Anstivirus threat with id '{_threatId++}' reported\n");
            }
        }
    }
}