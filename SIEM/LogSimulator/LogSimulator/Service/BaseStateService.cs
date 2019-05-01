using LogSimulator.Service.Interface;
using Microsoft.Extensions.Configuration;

namespace LogSimulator.Service
{
    public class BaseStateService : IBaseStateService
    {
        protected readonly IViewService _viewService;
        protected readonly IStateFactory _stateFactory;
        protected readonly IConfigurationRoot _configurationRoot;

        protected BaseStateService(
            IConfigurationRoot configurationRoot,
            IViewService viewService,
            IStateFactory stateFactory)
        {
            _configurationRoot = configurationRoot;
            _viewService = viewService;
            _stateFactory = stateFactory;
        }
    }
}
