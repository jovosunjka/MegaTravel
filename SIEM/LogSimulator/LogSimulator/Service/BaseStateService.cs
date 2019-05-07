using LogSimulator.Service.Interface;

namespace LogSimulator.Service
{
    public class BaseStateService : IBaseStateService
    {
        protected readonly IViewService _viewService;
        protected readonly IStateFactory _stateFactory;
        protected readonly IAppSettings _appSettings;
        protected readonly ILogService _logService;

        protected BaseStateService(
            IAppSettings appSettings,
            IViewService viewService,
            IStateFactory stateFactory,
            ILogService logService)
        {
            _appSettings = appSettings;
            _viewService = viewService;
            _stateFactory = stateFactory;
            _logService = logService;
        }
    }
}
