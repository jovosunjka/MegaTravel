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
                var option = _viewService.ReadKey();

                if(!char.IsNumber(option))
                {
                    _viewService.PrintInvalidInput();
                    continue;
                }

                if(IsBackOptionChosen(option))
                {
                    return;
                }

                RunChosenOption(option);
            }
        }

        private bool IsBackOptionChosen(char option)
        {
            if (char.GetNumericValue(option) == Enum.GetNames(typeof(StateType)).Length + 1)
            {
                if (_cancellationTokenSource != null)
                {
                    _cancellationTokenSource.Cancel(false);
                }
                return true;
            }
            return false;
        }

        private void RunChosenOption(char option)
        {
            var stateType = (StateType)(char.GetNumericValue(option) - 1);
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
