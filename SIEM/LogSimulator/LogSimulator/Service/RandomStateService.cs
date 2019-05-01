using LogSimulator.Service.Interface;
using Microsoft.Extensions.Configuration;
using System;
using System.IO;
using System.Threading;

namespace LogSimulator.Service
{
    public class RandomStateService : BaseStateService, IRandomStateService
    {
        public RandomStateService(
            IConfigurationRoot configurationRoot, 
            IViewService viewService, 
            IStateFactory stateFactory) 
            : base(configurationRoot, viewService, stateFactory)
        {
        }

        public void Start()
        {
            var cancellationTokenSource = new CancellationTokenSource();
            var randomThread = new Thread(new ParameterizedThreadStart(StartRandomService));
            randomThread.Start(cancellationTokenSource.Token);

            _viewService.PrintRandomServiceStarted();
            _viewService.PrintPressAnyKeyToStopService();
            Console.ReadKey();
            cancellationTokenSource.Cancel(false);
        }

        private void StartRandomService(object cancellationToken)
        {
            var logFolder = _configurationRoot.GetSection("Path")["LogFolderPath"];
            var logFilePath = Path.Combine(logFolder, $"Logs_{DateTime.UtcNow.ToShortDateString()}.txt");
            var sleepMilliseconds = int.Parse(_configurationRoot.GetSection("AppConfiguration")["SleepMilliseconds"]);
            while (true)
            {
                if(((CancellationToken)cancellationToken).IsCancellationRequested)
                {
                    return;
                }
                var currentState = _stateFactory.GetRandomState();
                currentState.Simulate(logFilePath);
                Thread.Sleep(sleepMilliseconds);
            }
        }
    }
}
