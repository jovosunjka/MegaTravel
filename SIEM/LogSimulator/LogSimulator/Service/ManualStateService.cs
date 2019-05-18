using LogSimulator.Helper;
using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;
using LogSimulator.State;
using System;
using System.Threading;

namespace LogSimulator.Service
{
    public class ManualStateService : BaseStateService, IManualStateService
    {
        private CancellationTokenSource _cancellationTokenSource;
        private IState _currentState;

        public ManualStateService(
            IAppSettings appSettings,
            IViewService viewService,
            IStateFactory stateFactory,
            ILogService logService)
            : base(appSettings, viewService, stateFactory, logService)
        {
        }

        public void ShowMenu()
        {
            while(true)
            {
                _viewService.ShowManualMenu();
                var input = _viewService.ReadLine();

                int.TryParse(input, out int choiceNum);
                if(choiceNum == 0)
                {
                    _viewService.PrintInvalidInput();
                    continue;
                }

                if(IsBackOptionChosen(choiceNum))
                {
                    return;
                }

                var stateNum = Enum.GetNames(typeof(StateType)).Length + 1;
                if(choiceNum > stateNum)
                {
                    _viewService.PrintInvalidInput();
                    continue;
                }

                RunChosenOption(choiceNum);
            }
        }

        private bool IsBackOptionChosen(int option)
        {
            if (option == Enum.GetNames(typeof(StateType)).Length + 1)
            {
                if (_cancellationTokenSource != null)
                {
                    _cancellationTokenSource.Cancel(false);
                }
                return true;
            }
            return false;
        }

        private void RunChosenOption(int option)
        {
            var stateType = (StateType)(option - 1);
            try
            {
                _currentState = _stateFactory.GetState(stateType);
            }
            catch (NoValidStateException)
            {
                _viewService.PrintInvalidInput();
                return;
            }

            _viewService.PrintEnteredState(_currentState.Description);
            _cancellationTokenSource = new CancellationTokenSource();
            var randomThread = new Thread(new ParameterizedThreadStart(ExecuteState));
            randomThread.Start(_cancellationTokenSource.Token);
        }

        private void ExecuteState(object cancellationToken)
        {
            if (((CancellationToken)cancellationToken).IsCancellationRequested)
            {
                return;
            }
            _currentState.Simulate(_appSettings, _logService);
        }
    }
}
