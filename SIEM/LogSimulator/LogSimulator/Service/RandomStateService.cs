using LogSimulator.Service.Interface;
using System;
using System.Threading;

namespace LogSimulator.Service
{
    public class RandomStateService : BaseStateService, IRandomStateService
    {
        public RandomStateService(
            IAppSettings appSettings, 
            IViewService viewService, 
            IStateFactory stateFactory) 
            : base(appSettings, viewService, stateFactory)
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
            var sleepMilliseconds = int.Parse(_appSettings.SleepMilliseconds);
            while (true)
            {
                if(((CancellationToken)cancellationToken).IsCancellationRequested)
                {
                    return;
                }
                var currentState = _stateFactory.GetRandomState();
                currentState.Simulate(_appSettings);
                Thread.Sleep(sleepMilliseconds);
            }
        }
    }
}
