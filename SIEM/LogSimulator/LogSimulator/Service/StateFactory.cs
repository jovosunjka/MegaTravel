using LogSimulator.Helper;
using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;
using LogSimulator.State;
using System;
using System.Linq;

namespace LogSimulator.Service
{
    public class StateFactory : IStateFactory
    {
        private readonly IAppSettings _appSettings;
        private readonly IStateService _stateService;

        public StateFactory(IAppSettings appSettings, IStateService stateService)
        {
            _appSettings = appSettings;
            _stateService = stateService;
        }

        private static int _counter = 1;

        public IState GetRandomState()
        {
            try
            {
                var stateType = StateType.NoAlarm;
                var alarmPossibility = int.Parse(_appSettings.AlarmPossibility);
                // make lower chance for some alarm state
                if (_counter % alarmPossibility == 0)
                {
                    var myEnumMemberCount = Enum.GetNames(typeof(StateType)).Length;
                    var randomEnumMemberNum = new Random().Next(myEnumMemberCount);
                    stateType = (StateType)randomEnumMemberNum;
                }
                return GetState(stateType);
            }
            finally
            {
                _counter++;
            }
        }

        public IState GetState(StateType stateType)
        {
            var stateName = stateType.ToString() + Constants.State;
            var type = _stateService.StateTypes.First(x => x.Name.Equals(stateName));
            return (IState)Activator.CreateInstance(type);
        }
    }
}
