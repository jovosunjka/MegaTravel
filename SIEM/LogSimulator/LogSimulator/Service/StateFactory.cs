using LogSimulator.Helper;
using LogSimulator.Model.Enum;
using LogSimulator.Service.Interface;
using LogSimulator.State;
using Microsoft.Extensions.Configuration;
using System;

namespace LogSimulator.Service
{
    public class StateFactory : IStateFactory
    {
        private readonly IConfigurationRoot _configurationRoot;

        public StateFactory(IConfigurationRoot configurationRoot)
        {
            _configurationRoot = configurationRoot;
        }

        private static int _counter = 1;

        public IState GetRandomState()
        {
            try
            {
                var stateType = StateType.NoAlarm;
                var alarmPossibility = int.Parse(_configurationRoot.GetSection("AppConfiguration")["AlarmPossibility"]);
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
            switch(stateType)
            {
                case StateType.NoAlarm:
                    return new NoAlarmState();
                case StateType.LogError:
                    return new LogErrorState();
                default:
                    throw new NoValidStateException();
            }
        }
    }
}
