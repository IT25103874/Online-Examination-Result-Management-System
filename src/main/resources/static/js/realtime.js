// js/realtime.js

export const realtime = (() => {
  const activePolls = new Map();
  let globalVisibilityHandlerRegistered = false;

  const handleVisibilityChange = () => {
    const isHidden = document.visibilityState === 'hidden';
    
    activePolls.forEach((pollConfig, id) => {
      if (isHidden) {
        if (pollConfig.timerId) {
          clearInterval(pollConfig.timerId);
          pollConfig.timerId = null;
          console.log(`Polling ID "${id}" paused (tab inactive)`);
        }
      } else {
        if (!pollConfig.timerId) {
          // Trigger immediate update on tab activation, then restart interval
          pollConfig.callback();
          pollConfig.timerId = setInterval(pollConfig.callback, pollConfig.intervalMs);
          console.log(`Polling ID "${id}" resumed (tab active)`);
        }
      }
    });
  };

  const start = (id, callback, intervalMs = 5000) => {
    // If a poll with this id already exists, stop it first to prevent duplicates/leaks
    stop(id);

    // Initial fetch/callback run immediately
    callback();

    const timerId = setInterval(callback, intervalMs);
    activePolls.set(id, {
      callback,
      intervalMs,
      timerId
    });

    // Lazy register visibility handler once
    if (!globalVisibilityHandlerRegistered) {
      document.addEventListener('visibilitychange', handleVisibilityChange);
      globalVisibilityHandlerRegistered = true;
    }

    console.log(`Polling ID "${id}" started with interval ${intervalMs}ms`);
  };

  const stop = (id) => {
    if (activePolls.has(id)) {
      const pollConfig = activePolls.get(id);
      if (pollConfig.timerId) {
        clearInterval(pollConfig.timerId);
      }
      activePolls.delete(id);
      console.log(`Polling ID "${id}" stopped and cleared`);
    }
  };

  const stopAll = () => {
    activePolls.forEach((pollConfig, id) => {
      if (pollConfig.timerId) {
        clearInterval(pollConfig.timerId);
      }
    });
    activePolls.clear();
    console.log('All active polling processes cleared.');
  };

  return {
    start,
    stop,
    stopAll
  };
})();
