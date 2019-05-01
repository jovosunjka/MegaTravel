using LogSimulator.Helper;
using System.IO;

namespace LogSimulator.State
{
    public class NoAlarmState : IState
    {
        public string Description => Constants.StateDescription.NoAlarm;

        public void Simulate(string logFilePath)
        {
            File.AppendAllText(logFilePath, $"{Constants.StateDescription.NoAlarm} started\n");
        }
    }
}
