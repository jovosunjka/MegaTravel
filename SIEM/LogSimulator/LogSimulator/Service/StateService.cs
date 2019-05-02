using System.Reflection;
using System.Linq;
using System.Collections.Generic;
using System;
using LogSimulator.Model.Enum;
using LogSimulator.Helper;
using LogSimulator.Service.Interface;

namespace LogSimulator.Service
{
    public class StateService : IStateService
    {
        public List<Type> StateTypes { get; private set; }
        public List<string> StateDescriptions { get; private set; }

        public StateService()
        {
            // reflection is slow, states will not be changed in runtime
            // so we use properties, instead of calling methods each time
            StateTypes = GetStateTypes();
            StateDescriptions = GetSortedStateDescriptions();
        }

        private List<Type> GetStateTypes()
        {
            var namespaceName = Constants.StatesNamespace;
            var states = from type in Assembly.GetExecutingAssembly().GetTypes()
                         where type.IsClass && type.Namespace == namespaceName
                         select type;

            return states.ToList();
        }

        private List<string> GetSortedStateDescriptions()
        {
            var stateDescSorted = new List<string>();
            var enums = Enum.GetValues(typeof(StateType));
            var descriptions = typeof(Constants.StateDescription).GetFields();
            foreach (var enumVal in enums)
            {
                var description = descriptions.FirstOrDefault(x => x.Name.Equals(enumVal.ToString()));
                var descValue = (string)description.GetValue(null);
                stateDescSorted.Add(descValue);
            }

            return stateDescSorted;
        }
    }
}
