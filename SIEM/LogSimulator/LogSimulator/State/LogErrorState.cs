using LogSimulator.Helper;
using System.IO;

namespace LogSimulator.State
{
    public class LogErrorState : IState
    {
        public string Description => Constants.StateDescription.LogError;

        public void Simulate(string logFilePath)
        {
            File.AppendAllText(logFilePath, $"{Helper.Constants.StateDescription.LogError} started\n");
        }
    }
}
