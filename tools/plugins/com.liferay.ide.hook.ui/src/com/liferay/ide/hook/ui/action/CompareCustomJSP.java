package com.liferay.ide.hook.ui.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.internal.AbstractEvaluationHandler;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class CompareCustomJSP extends AbstractEvaluationHandler
{
    private Expression enabledWhen;

    public CompareCustomJSP()
    {
        registerEnablement();
    }

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        System.out.println(event);
        return null;
    }

    @Override
    protected Expression getEnabledWhenExpression()
    {
        if( enabledWhen == null )
        {
            enabledWhen = new Expression()
            {
                @Override
                public EvaluationResult evaluate( IEvaluationContext context ) throws CoreException
                {
                    return EvaluationResult.TRUE;
                }
            };
        }

        return enabledWhen;
    }

}
