package org.geogebra.common.main.settings.config;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.equationforms.DefaultEquationBehaviour;
import org.geogebra.common.main.settings.updater.SettingsUpdater;

abstract class AbstractAppConfig implements AppConfig {

    private String appCode;
    private String subAppCode;
    protected transient CommandFilter commandFilter;
    protected EquationBehaviour equationBehaviour;

    AbstractAppConfig(String appCode) {
        this(appCode, null);
    }

    AbstractAppConfig(String appCode, String subAppCode) {
        this.appCode = appCode;
        this.subAppCode = subAppCode;
        setEquationBehaviour();
    }

    @Override
    public String getAppCode() {
        return appCode;
    }

    @CheckForNull
    @Override
    public String getSubAppCode() {
        return subAppCode;
    }

    @Nonnull
    @Override
    public final EquationBehaviour getEquationBehaviour() {
        return equationBehaviour;
    }

    @Override
    public void setEquationBehaviour() {
        equationBehaviour = new DefaultEquationBehaviour();
    }

    @Override
    public SettingsUpdater createSettingsUpdater() {
        return new SettingsUpdater();
    }

    @Override
    public String getAppTransKey() {
        if (getSubAppCode() != null) {
            return  GeoGebraConstants.Version.SUITE.getTransKey();
        }
        return getVersion().getTransKey();
    }

    @Override
    public int getMainGraphicsViewId() {
        return App.VIEW_EUCLIDIAN;
    }

	@Override
	public boolean hasOneVarStatistics() {
		return true;
	}

    @Override
    public CommandFilter getCommandFilter() {
        if (commandFilter == null) {
            commandFilter = createCommandFilter();
        }
        return commandFilter;
    }

    @Override
    public boolean hasDataImport() {
        return true;
    }

    @Override
    public void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
            @CheckForNull ExamType examType) {
        if (featureRestrictions.contains(ExamFeatureRestriction.RESTRICT_CHANGING_EQUATION_FORM)) {
            equationBehaviour.allowChangingEquationFormsByUser(false);
        }
    }

    @Override
    public void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions) {
        setEquationBehaviour();
    }
}
